package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import model.Pieces.Bishop;
import model.Pieces.King;
import model.Pieces.Knight;
import model.Pieces.Pawn;
import model.Pieces.Queen;
import model.Pieces.Rook;

public final class StrictChessModel implements MutableChessModel {
  //Constants
  //board dimensions
  public static final int NUM_RANKS = 8;
  public static final int NUM_FILES = 8;
  //the number of sections of information in a fen string
  private static final int NUM_FEN_PARTS = 6;
  //FEN for the starting position
  public static final String STARTING_POSITION =
          "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  private static final int HASHING_PRIME_NUMBER = 31; //used for hashCode()
  //the set of all legal promotion pieces, initialized in static block
  //Since this is an unmodifiable set and each type is immutable, this is safe to be public
  public static final Set<PieceType> pawnPromotionPieceTypes;

  //fields for a unique chess model instance
  private String fenString; //INVARIANT: fenString is always valid and up to date
  private final Optional<Piece>[][] gameBoard;
  private final List<ModelListener> listeners;
  private boolean hasGameStarted;
  //board rights: represents the rights for each color in a given board
  private boolean whiteToMove;
  //INVARIANT: castlingRights and enPassantTargetSquare are never null
  private String castlingRights;
  private String enPassantTargetSquare;
  //INVARIANT: halfMoveClock > 0 and fullMoveClock > 0
  private int halfMoveClock;
  private int fullMoveClock;


  static {
    //create an unmodifiable set containing the 4 legal promotion pieces
    pawnPromotionPieceTypes = Set.of(
            PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN
    );
  }

  //Private constructor to force client instantiation throught the builder
  @SuppressWarnings("unchecked")
  private StrictChessModel(Builder builder) {
    //Set the game board to an empty 8 x 8 array
    //We are sure that this is a type-safe cast, so we can suppress the warning
    this.gameBoard = (Optional<Piece>[][]) new Optional<?>[NUM_RANKS][NUM_FILES];
    //try to initialize the board from the fen string, throw an IAE if invalid
    this.fenString = tryInitializingBoardFromFen(builder.fen);
    this.listeners = new ArrayList<>();
    this.hasGameStarted = false;
  }

  public static class Builder {
    //Required parameters - initialized in public constructor
    private final String fen;

    public Builder(String fen) {
      if (fen == null) {
        throw new IllegalArgumentException("Unable to create model builder with null fenString");
      }
      this.fen = fen;
    }

    public StrictChessModel build() {
      return new StrictChessModel(this);
    }
  }

  private String tryInitializingBoardFromFen(String fen) {
    try {
      //FEN Strings contain 6 pieces of information, each separated by spaces:
      String[] parts = fen.split(" ");
      if (parts.length != NUM_FEN_PARTS) {
        throw new IllegalArgumentException(String.format("Invalid part length: expected %d piece of " +
                "information, got %d parts", NUM_FEN_PARTS, parts.length));
      }
      tryToParsePiecePlacement(parts[0]); //parts[0] contains info for the piece placement
      tryToParseSideToMove(parts[1]); //parts[1] contains info for side to move
      tryToParseCastlingPrivs(parts[2]); //parts[2] contains info for castling privileges
      tryToParseEnPassantTargetSquare(parts[3]); //parts[3] contains info for en-passant target square
      tryToParseHalfMoveClock(parts[4]); //parts[4] contains info for half move clock
      tryToParseFullMoveClock(parts[5]); //parts[5] contains info for full move clock
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to parse FEN String due to error %s",
              e.getMessage() == null ? "" : e.getMessage()));
    }
    return fen;
  }

  /**
   * Initializes the game board information for the given fen string.
   * A piece placement section of the fen string is legal if and only if it encodes...
   * <ol>
   *   <li>
   *     Tile information for no more and no less than all 8 x 8 = 64 tiles
   *   </li>
   *   <li>
   *     Each tile either is empty or contains a valid chess piece
   *   </li>
   *   <li>
   *     Each board contains exactly one black king and one white king
   *   </li>
   * </ol>
   *
   * @param piecePlacement the piece placement section of the fen string
   * @throws IllegalArgumentException if the piece placement is illegal
   */
  private void tryToParsePiecePlacement(String piecePlacement) {
    String[] ranks = piecePlacement.split("/");
    if (ranks.length != StrictChessModel.NUM_RANKS) { //there should be exactly 8 ranks for a ches board
      throw new IllegalArgumentException(String.format("Invalid piece placement: expected info " +
              "for %d ranks, got info for %d ranks", NUM_RANKS, ranks.length));
    }
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      String rankChars = ranks[rank];
      int filesWritten = 0; //keep track of the files we write to - ensure this is 8
      for (int file = 0; file < NUM_FILES && file < rankChars.length(); file++) {
        char currPos = rankChars.charAt(file);
        //if the current position is a digit, we want to add that number of empty spaces
        if (Character.isDigit(currPos)) {
          int numEmptySpaces = Character.getNumericValue(currPos);
          int emptySpacesAdded = 0;
          while (emptySpacesAdded < numEmptySpaces) {
            //add the empty optional in our game-board to denote an empty space
            gameBoard[rank][file++] = Optional.empty();
            filesWritten++;
            emptySpacesAdded++;
          }
        } //if the current position is not a digit, it must specify a piece
        else {
          Optional<Piece> piece = fenCharToPieceFactory(currPos);
          if (piece.isPresent()) {
            gameBoard[rank][file] = piece;
            filesWritten++;
          } else {
            throw new IllegalArgumentException(String.format(
                    "%c is an unknown fen character to create a piece", currPos));
          }
        }
      }
      if (filesWritten != NUM_FILES) { //Ensure each rank has info for exactly 8 files
        throw new IllegalArgumentException(String.format("Invalid piece placement: expected " +
                        "%d files of information for rank %d, got %d files of information",
                NUM_FILES, rank, filesWritten));
      }
    }
    int whiteKingCount = countOccurrences(piecePlacement, 'K');
    int blackKingCount = countOccurrences(piecePlacement, 'k');
    if (whiteKingCount != 1 || blackKingCount != 1) {
      throw new IllegalArgumentException("Invalid piece placement: does not contain" +
              "exactly one white one black king");
    }
  }

  private void tryToParseSideToMove(String sideToMove) {
    if (sideToMove == null || !(sideToMove.equals("w") || sideToMove.equals("b"))) {
      throw new IllegalArgumentException(String.format("Invalid side top move character - expected" +
              "w or b but got %s", sideToMove));
    }
    this.whiteToMove = sideToMove.equals("w");
  }

  private void tryToParseCastlingPrivs(String castlingPrivs) {
    if (castlingPrivs.equals("-")) {
      this.castlingRights = "-";
    } else {
      StringBuilder sb = new StringBuilder();
      for (char c : castlingPrivs.toCharArray()) {
        int index = "KQkq".indexOf(c);
        if (index == -1) {
          throw new IllegalArgumentException(String.format("%s is not a recognized caslting privilege configuration", castlingPrivs));
        } else {
          sb.append("KQkq".charAt(index));
        }
      }
      this.castlingRights = sb.toString();
    }
  }

  private void tryToParseEnPassantTargetSquare(String enPassantTarget) {
    if (enPassantTarget.equals("-")) {
      this.enPassantTargetSquare = enPassantTarget;
    } else if (enPassantTarget.length() == 2 &&
            enPassantTarget.charAt(0) >= 'a' && enPassantTarget.charAt(0) <= 'h' &&
            enPassantTarget.charAt(1) >= '1' && enPassantTarget.charAt(1) <= '8') {
      this.enPassantTargetSquare = enPassantTarget;
    } else {
      throw new IllegalArgumentException(String.format(
              "%s is an invalid en passant target square", enPassantTarget));
    }
  }

  private void tryToParseHalfMoveClock(String halfMoveClock) {
    try {
      int halfMoveCounter = Integer.parseInt(halfMoveClock);
      if (halfMoveCounter < 0) {
        throw new IllegalArgumentException(String.format(
                "%s is an invalid half move counter", halfMoveClock));
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(String.format(
              "%s is an invalid half move counter", halfMoveClock));
    }
    this.halfMoveClock = Integer.parseInt(halfMoveClock);
  }

  private void tryToParseFullMoveClock(String fullMoveClock) {
    try {
      int fullMoveCounter = Integer.parseInt(fullMoveClock);
      if (fullMoveCounter < 0) {
        throw new IllegalArgumentException(String.format(
                "%s is an invalid full Move Clock", fullMoveClock));
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(String.format(
              "%s is an invalid full Move Clock", fullMoveClock));
    }
    this.fullMoveClock = Integer.parseInt(fullMoveClock);
  }


  @Override
  public String toString() {
    return this.fenString;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof StrictChessModel otherModel) {
      return this.fenString.equals(other.toString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fenString);
  }

  @Override
  public void makeMove(Move m) {
    if (!canMakeMove(m)) {
      throw new IllegalArgumentException("Unable to make move due to null or illegal move");
    }
    //If we are here, the move is legal. We are safe to retrieve the src, dest, and the piece
    //This means that the source contains a piece, and the destination tile is an either unoccupied
    //or contains an enemy piece that legally can be captured.
    RowColPair sourcePos = m.getSourcePosition();
    Move.MoveFlag flag = m.getFlag();
    switch (flag) {
      case PAWN_PROMOTION_TO_ROOK -> makePawnPromotionMove(m, PieceType.ROOK);
      case PAWN_PROMOTION_TO_BISHOP -> makePawnPromotionMove(m, PieceType.BISHOP);
      case PAWN_PROMOTION_TO_KNIGHT -> makePawnPromotionMove(m, PieceType.KNIGHT);
      case PAWN_PROMOTION_TO_QUEEN -> makePawnPromotionMove(m, PieceType.QUEEN);
      case EN_PASSANT -> makeEnPassantMove(m);
      case CASTLE_KINGSIDE, CASTLE_QUEENSIDE -> makeCastlingMove(m);
      case NONE, DOUBLE_PAWN_PUSH, KING_MOVE, ROOK_MOVE -> makeSimpleMove(m);
      default -> throw new IllegalStateException("Unable to make : " + flag);
    }
    updateFenString(m);
    notifyAllListeners(ModelEvent.MOVE_MADE);
  }

  private void makeSimpleMove(Move m) {
    RowColPair sourcePos = m.getSourcePosition();
    RowColPair destPos = m.getDestinationPosition();
    Optional<Piece> piece = gameBoard[sourcePos.getRow()][sourcePos.getCol()];
    //put the source piece at the destination slot
    gameBoard[destPos.getRow()][destPos.getCol()] = piece;

    //clear the source piece from its source slot
    gameBoard[sourcePos.getRow()][sourcePos.getCol()] = Optional.empty();
  }

  /**
   * If m is a move of a pawn to a promotion square, makes the pawn promotion move. Otherwise,
   * throws an IllegalArgumentException.
   *
   * @param m    the move to be made, must be a legal move
   * @param type the type of piece to promote to
   * @throws IllegalArgumentException if the move is a move to a promotion square but there is
   *                                  an error in creating the newly promoted piece
   * @throws IllegalArgumentException if the move is not legal
   */
  private void makePawnPromotionMove(Move m, PieceType type) {
    if (!canMakeMove(m)) {
      throw new IllegalArgumentException("Unable to try pawn promotino move with illegal move");
    }
    Move.MoveFlag flag = m.getFlag();
    //If we are here, we have a legal pawn promotion move
    RowColPair sourcePos = m.getSourcePosition();
    RowColPair destPos = m.getDestinationPosition();
    Piece piece = gameBoard[sourcePos.getRow()][sourcePos.getCol()].get();
    //create the new promotion piece and put it at the destination tile
    char typeChar = type.getLowercasedPieceID();
    if (piece.getIsWhite()) { //if we are promoting to a white piece, ensure promotion piece is white
      typeChar = Character.toUpperCase(typeChar);
    }
    Optional<Piece> newPiece = fenCharToPieceFactory(typeChar);
    if (newPiece.isEmpty()) {
      throw new IllegalStateException("Cannot make pawn promotion move due to invalid or " +
              "unspecified promotion piece type");
    }
    gameBoard[destPos.getRow()][destPos.getCol()] = newPiece;
    //clear the source piece
    gameBoard[sourcePos.getRow()][sourcePos.getCol()] = Optional.empty();
  }

  /**
   * If m is an en-passsant move, makes the en-passant move.
   *
   * @param m the move to be made, must be a legal move
   * @throws IllegalArgumentException if the move is not legal
   */
  private void makeEnPassantMove(Move m) {
    if (!canMakeMove(m)) {
      throw new IllegalArgumentException("Unable to try en passant move with illegal move");
    }
    if (m.getFlag() != Move.MoveFlag.EN_PASSANT) {
      throw new IllegalArgumentException();
    }
    //If we are here, we have a legal en-passant move
    RowColPair sourcePos = m.getSourcePosition();
    RowColPair destPos = m.getDestinationPosition();
    Optional<Piece> piece = gameBoard[sourcePos.getRow()][sourcePos.getCol()];
    //put the source piece at the destination slot
    gameBoard[destPos.getRow()][destPos.getCol()] = piece;

    //clear the source piece from its source slot
    gameBoard[sourcePos.getRow()][sourcePos.getCol()] = Optional.empty();

    //remove the en-passant piece that was captured
    //we know the captured piece must be the piece at the en passant target square
    //we know this is safe to retreive, since the move is legal
    RowColPair enPassantTarget = getEnPassantTarget().get();
    gameBoard[enPassantTarget.getRow()][enPassantTarget.getCol()] = Optional.empty();
  }

  private void makeCastlingMove(Move m) {
    if (!canMakeMove(m)) {
      throw new IllegalArgumentException("Unable to try en passant move with illegal move");
    }
    Move.MoveFlag flag = m.getFlag();
    if (!King.castlingFlags.contains(flag)) {
      throw new IllegalArgumentException("Unable to make castling move with non castling flag");
    }
    //If we are here, we have a legal castling move
    RowColPair sourcePos = m.getSourcePosition();
    RowColPair destPos = m.getDestinationPosition();
    Piece king = gameBoard[sourcePos.getRow()][sourcePos.getCol()].get();
    //put the king at the destination slot
    gameBoard[destPos.getRow()][destPos.getCol()] = Optional.of(king);
    //set the old king slot to empty
    gameBoard[sourcePos.getRow()][sourcePos.getCol()] = Optional.empty();
    //find the source and destintaion position for the rook
    RowColPair rookHomeSquare, rookDestination;
    if (flag == Move.MoveFlag.CASTLE_KINGSIDE) {
      rookHomeSquare = king.isWhite ? new RowColPair(7, 7) : new RowColPair(0, 7);
      rookDestination = king.isWhite ? new RowColPair(7, 5) : new RowColPair(0, 5);
    } else { //if we are here, we are castling queenside
      rookHomeSquare = king.isWhite ? new RowColPair(7, 0) : new RowColPair(0, 0);
      rookDestination = king.isWhite ? new RowColPair(7, 3) : new RowColPair(0, 3);
    }
    //place the rook at the destination square
    Optional<Piece> rook = gameBoard[rookHomeSquare.getRow()][rookHomeSquare.getCol()];
    if (rook.isEmpty()) {
      throw new IllegalArgumentException("Unable ot make castling move without rook at home square");
    }
    gameBoard[rookDestination.getRow()][rookDestination.getCol()] = rook;
    //clear the rook source square
    gameBoard[rookHomeSquare.getRow()][rookHomeSquare.getCol()] = Optional.empty();
  }

  @Override
  public void startGame() {
    if (hasGameStarted ) {
      throw new IllegalStateException("Unable to start game that is already in progress or over");
    }
    this.hasGameStarted = true;
    notifyAllListeners(ModelEvent.GAME_STARTED);
  }

  @Override
  public boolean canMakeMove(Move m) {
    ensureGameInProgress();
    if (m == null) {
      throw new IllegalArgumentException("Cannot check move for null move");
    }
    checkIfPositionIsValid(m.getSourcePosition());
    checkIfPositionIsValid(m.getDestinationPosition());
    RowColPair sourcePos = m.getSourcePosition();
    RowColPair destPos = m.getDestinationPosition();
    Optional<Piece> piece = gameBoard[sourcePos.getRow()][sourcePos.getCol()];
    if (piece.isEmpty()) {
      throw new IllegalArgumentException("Cannot check move for empty source square");
    }
    Set<RowColPair> targetPositions = piece.get().getTargetSquares(sourcePos, getPermissibleDeepCopy());
    return targetPositions.contains(destPos);
  }

  @Override
  public boolean isGameOver() {
    if (!hasGameStarted) {
      throw new IllegalStateException("Unable to get game over for game not yet in progress");
    }
    PlayerColor turn = whiteToMove ? PlayerColor.WHITE : PlayerColor.BLACK;
    return getLegalMoves(turn).isEmpty(); //INVARIANT: getLegalMoves never returns null
  }

  @Override
  public GameResultStatus getFinalGameStatus() {
    if (!isGameOver()) {
      throw new IllegalStateException("Unable to retrieve final game status for game that is not yet over");
    }
    if (this.whiteToMove) {
      //if it is white's turn, the game is over iff they have no legal moves
      //if they are in check, it is a checkmate for black, otherwise it is some form of draw
      if (getColorTargetSquares(PlayerColor.BLACK).contains(getKingSquare(PlayerColor.WHITE))) {
        return GameResultStatus.CHECKMATE_BY_BLACK;
      } else {
        return GameResultStatus.STALEMATE;
      }
    } else {
      //if it is black's turn, the game is over iff they have no legal moves
      //if they are in check, it is a checkmate for white, otherwise it is some form of draw
      if (getColorTargetSquares(PlayerColor.WHITE).contains(getKingSquare(PlayerColor.BLACK))) {
        return GameResultStatus.CHECKMATE_BY_WHITE;
      } else {
        return GameResultStatus.STALEMATE;
      }
    }
  }

  @Override
  public RowColPair getKingSquare(PlayerColor c) {
    ensureColorValidity(c);
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      for (int file = 0; file < NUM_FILES; file++) {
        Optional<Piece> piece = gameBoard[rank][file];
        if (piece.isPresent() && isKingMatchingColor(piece.get(), c)) {
          return new RowColPair(rank, file);
        }
      }
    }
    throw new IllegalStateException(String.format(
            "GameBoard did not contain any kings of color %s", c));
  }

  private boolean isKingMatchingColor(Piece piece, PlayerColor c) {
    //INVARIANT: piece is not null and color is a valid color
    boolean colorMatches = ((c == PlayerColor.WHITE) == piece.getIsWhite());
    return piece.getType() == PieceType.KING && colorMatches;
  }

  @Override
  public boolean getWhiteToMove() {
    return this.whiteToMove;
  }

  @Override
  public StrictChessModel getStrictDeepCopy() {
    StrictChessModel copy = new Builder(this.fenString).build();
    if(hasGameStarted){
      copy.startGame();
    }
    //no need to copy over the listeners, since they are only concerned with this model
    return copy;
  }

  @Override
  public PermissiveChessModel getPermissibleDeepCopy(){
    return new PermissibleChessModelImpl(this.getStrictDeepCopy());
  }

  @Override
  public void addListener(ModelListener listener) {
    this.listeners.add(listener);
  }

  private void notifyAllListeners(ModelEvent event) {
    for (ModelListener m : this.listeners) {
      m.notifyAfterModelUpdate(event);
    }
  }

  @Override
  public Set<Move> getLegalMoves(PlayerColor c) {
    ensureColorValidity(c);
    boolean isWhite = c == PlayerColor.WHITE;
    Set<Move> legalMoves = new HashSet<>();
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      for (int file = 0; file < NUM_FILES; file++) {
        Optional<Piece> piece = gameBoard[rank][file];
        if (piece.isPresent() && (piece.get().getIsWhite() == isWhite)) {
          RowColPair position = new RowColPair(rank, file);
          legalMoves.addAll(piece.get().getLegalMoves(position, this));
        }
      }
    }
    return legalMoves;
  }

  @Override
  public Set<RowColPair> getColorTargetSquares(PlayerColor c) {
    ensureColorValidity(c);
    boolean isWhite = c == PlayerColor.WHITE;
    Set<RowColPair> colorTargetSquares = new HashSet<>();
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      for (int file = 0; file < NUM_FILES; file++) {
        Optional<Piece> piece = gameBoard[rank][file];
        if (piece.isPresent() && (piece.get().getIsWhite() == isWhite)) {
          RowColPair position = new RowColPair(rank, file);
          colorTargetSquares.addAll(piece.get().getTargetSquares(position, this));
        }
      }
    }
    return colorTargetSquares;
  }

  @Override
  public Optional<Set<RowColPair>> getTargetSquares(RowColPair position) {
    checkIfPositionIsValid(position);
    Optional<Piece> piece = gameBoard[position.getRow()][position.getCol()];
    //if the piece is present, returns the piece's target squares. Otherwise, returns the empty optional
    return piece.map(p -> p.getTargetSquares(position, this));
  }

  @Override
  public Optional<PieceType> getPieceTypeAt(RowColPair pair) {
    checkIfPositionIsValid(pair);
    Optional<Piece> piece = gameBoard[pair.getRow()][pair.getCol()];
    //if the piece is present, returns the piece's type. Otherwise, returns the empty optional
    return piece.map(Piece::getType);
  }

  @Override
  public String getLetterSquareCombination(RowColPair pair) {
    checkIfPositionIsValid(pair);
    String letters = "abcdefgh";
    return letters.substring(pair.getCol(), pair.getCol() + 1) + (8 - pair.getCol());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Piece>[][] getBoardCopy() {
    //We are sure that this is a type-safe cast, so we can suppress the warning
    Optional<Piece>[][] copy = (Optional<Piece>[][]) new Optional<?>[NUM_RANKS][NUM_FILES];
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      for (int file = 0; file < NUM_FILES; file++) {
        Optional<Piece> piece = gameBoard[rank][file];
        if (piece.isEmpty()) {
          copy[rank][file] = Optional.empty();
        } else { //Optional & pieces are immutable, so it safe to pass a reference to the same class
          copy[rank][file] = piece;
        }
      }
    }
    return copy;
  }

  private void checkIfPositionIsValid(RowColPair pair) {
    if (!isInBounds(pair)) {
      throw new IllegalArgumentException("Invalid rank or file - rank or file was not in " +
              "between 0 and 8 or row col pair was null");
    }
  }

  @Override
  public boolean isInBounds(RowColPair pair) {
    return pair != null && pair.getRow() >= 0 && pair.getRow() <= NUM_RANKS
            && pair.getCol() >= 0 && pair.getCol() <= NUM_FILES;
  }

  private static Optional<Piece> fenCharToPieceFactory(char fenChar) {
    return switch (fenChar) {
      case 'k' -> Optional.of(new King(false));
      case 'K' -> Optional.of(new King(true));
      case 'q' -> Optional.of(new Queen(false));
      case 'Q' -> Optional.of(new Queen(true));
      case 'b' -> Optional.of(new Bishop(false));
      case 'B' -> Optional.of(new Bishop(true));
      case 'p' -> Optional.of(new Pawn(false));
      case 'P' -> Optional.of(new Pawn(true));
      case 'n' -> Optional.of(new Knight(false));
      case 'N' -> Optional.of(new Knight(true));
      case 'r' -> Optional.of(new Rook(false));
      case 'R' -> Optional.of(new Rook(true));
      default -> Optional.empty();
    };
  }

  private static int countOccurrences(String s, char specifiedChar) {
    int count = 0;
    for (char c : s.toCharArray()) {
      if (c == specifiedChar) {
        count++;
      }
    }
    return count;
  }

  /**
   * Updates the fen string after a move is made by...
   * <ol>
   *   <li>
   *     Updates piece positioning
   *   </li>
   *   <li>
   *     Toggles the side to move
   *   </li>
   *   <li>
   *     Updates castling privileges if a king or rook move is made, or if a rook is captured.
   *   </li>
   *   <li>
   *     Sets/clears the en-passant target square based on if the moves was a
   *     double-tile pawn push
   *   </li>
   *   <li>
   *     Increments half move counter / full move clock
   *   </li>
   *   <li>
   *     Updates the map of position -> count to ensure that we are checking for threefold repetition
   *   </li>
   * </ol>
   *
   * @param m the move that was just made
   */
  private void updateFenString(Move m) {
//    updateEnPassantTargetSquare(m);
//    updateCastlingPrvileges(m);
    //if a pawn was moved, or a piece was captured, reset the half move clock
    //otherwise, increment the half move clock
    //if a rook is moved or catured, remove castling privileges for that rook's color on that side
    //if a king is moved, remove all castling privliegs for that king
    //if a double pawn push is made, set the en-passant target square to the square behind the pawn
    //otherwise, clear the en-passant target square
    //update the map of position -> count to ensure that we are checking for threefold repetition
    //toggle the side to move

    StringBuilder fenBuilder = new StringBuilder();
    for (int rank = 0; rank < NUM_RANKS; rank++) {
      int emptyTileCount = 0;
      for (int file = 0; file < NUM_FILES; file++) {
        Optional<Piece> currPiece = gameBoard[rank][file];
        if (currPiece.isEmpty()) {
          emptyTileCount++;
          continue;
        }
        if (emptyTileCount > 0) {
          fenBuilder.append(emptyTileCount);
          emptyTileCount = 0;
        }
        boolean isWhite = currPiece.get().getIsWhite();
        char pieceID = currPiece.get().getType().getLowercasedPieceID();
        fenBuilder.append(isWhite ? (Character.toUpperCase(pieceID)) : (pieceID));
      }
      if (emptyTileCount > 0) {
        fenBuilder.append(emptyTileCount);
      }
      fenBuilder.append('/');
    }
    //Add the baord rights for our board w/ each separated by a space
    fenBuilder.append(' ');
    fenBuilder.append(whiteToMove ? 'w' : 'b');
    fenBuilder.append(' ');
    fenBuilder.append(castlingRights);
    fenBuilder.append(' ');
    fenBuilder.append(enPassantTargetSquare.equals("") ? '-' : enPassantTargetSquare);
    fenBuilder.append(' ');
    fenBuilder.append(halfMoveClock);
    fenBuilder.append(' ');
    fenBuilder.append(fullMoveClock);
    this.fenString = fenBuilder.toString();
  }

  private void ensureGameInProgress() {
    if (!hasGameStarted) {
      throw new IllegalStateException("Game is over or not in progress yet!");
    }
  }

  private static void ensureColorValidity(PlayerColor c) {
    if (c != PlayerColor.WHITE && c != PlayerColor.BLACK) {
      throw new IllegalArgumentException("Invalid color passed, must be either black or white");
    }
  }

  @Override
  public String getCastlingPrivileges() {
    return this.castlingRights;
  }

  @Override
  public Optional<RowColPair> getEnPassantTarget() {
    return this.enPassantTargetSquare.equals("-") ? Optional.empty() :
            Optional.of(getRowColPairFromLetterCombination(this.enPassantTargetSquare));
  }

  public RowColPair getRowColPairFromLetterCombination(String letterCombination) {
    if (letterCombination.length() != 2) {
      throw new IllegalArgumentException("Unable to get row col pair from invalid letter combination");
    }
    String letters = "abcdefgh";
    int file = letters.indexOf(letterCombination.charAt(0));
    int rank = 8 - Character.getNumericValue(letterCombination.charAt(1));
    RowColPair conversion = new RowColPair(rank, file);
    checkIfPositionIsValid(conversion);
    return conversion;
  }

}
