package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ChessModel implements MutableChessModel {
  //Constants
  //board dimensions
  public static final int NUM_RANKS = 8;
  public static final int NUM_FILES = 8;
  //the number of sections of information in a fen string
  public static final int NUM_FEN_PARTS = 6;
  public static final String STARTING_POSITION =
          "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  public static final int HASHING_PRIME_NUMBER = 31; //used for hashCode()
  //the set of all legal promotion pieces, initialized in static block
  public static final Set<PieceType> pawnPromotionPieceTypes;

  //fields for a unique chess model instance
  private String fenString;
  private final Optional<Piece>[][] gameBoard;
  private final List<ModelListener> listeners;
  //board rights: represents the rights for each color in a  given board
  private boolean whiteToMove = true;
  private final char[] castlingRights = new char[4];
  //INVARIANT: enPassantTargetSquare is never null
  private String enPassantTargetSquare = "-";

  static {
    //create an unmodifiable set containing the 4 legal promotion pieces
    pawnPromotionPieceTypes = Set.of(
            PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN
    );
  }

  @SuppressWarnings("unchecked")
  private ChessModel(Builder builder) {
    //Set the game board to an empty 8 x 8 array
    //We are sure that this is a type-safe cast, so we can suppress the warning
    this.gameBoard = (Optional<Piece>[][]) new Optional<?>[NUM_RANKS][NUM_FILES];
    //try to initialize the board from the fen string, throw an IAE if invalid
    try {
      this.fenString = tryInitializingBoardFromFen(builder.fen);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unable to initialize chess model from the " +
              "given builder due to null or invalid fen string");
    }

    this.listeners = new ArrayList<>();
  }

  private String tryInitializingBoardFromFen(String fen) {
    try {
      //FEN Strings contain 6 pieces of information, each separated by spaces:
      String[] parts = fenString.split(" ");
      if (!(parts.length == NUM_FEN_PARTS)) {
        throw new IllegalArgumentException(String.format("Invalid FEN String: expected %d piece of " +
                "information, got %d parts", NUM_FEN_PARTS, parts.length));
      }
      parsePiecePlacement(parts[0]); //parts[0] contains info for the piece placement
      parseSideToMove(parts[1]); //parts[1] contains info for side to move
      parseCastlingPrivs(parts[2]); //parts[2] contains info for castling privileges
      parseEnPassantTarget(parts[3]); //parts[3] contains info for en-passant target square
      parseHalfMoveClock(parts[4]); //parts[4] contains info for half move clock
      parseFullMoveClock(parts[5]); //parts[5] contains info for full move clock
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Unable to parse FEN String due to error %s",
              e.getMessage() == null ? "" : e.getMessage()));
    }
    return "";
  }

  private void parsePiecePlacement(String piecePlacement) {
    String[] ranks = piecePlacement.split("/");

    for (int rank = 0; rank < NUM_RANKS; rank++) {
      String rankChars = ranks[rank];
      for (int file = 0; file < NUM_FILES && file < rankChars.length(); file++) {
        String currPos = rankChars.substring(file, file + 1);
        if (isParsable(currPos)) { //if the current position is a digit, we want to add that number of empty spaces
          int numEmptySpaces = Integer.parseInt(currPos);
          int emptySpacesAdded = 0;
          while (emptySpacesAdded < numEmptySpaces && file < NUM_FILES) {
            //add the empty optional in our game-board to denote an empty space
            gameBoard[rank][file++] = Optional.empty();
            emptySpacesAdded++;
          }
        }
      }
    }

  }

  private void parseSideToMove(String part) {

  }

  private void parseCastlingPrivs(String part) {
  }

  private void parseEnPassantTarget(String part) {

  }

  private void parseHalfMoveClock(String part) {
  }

  private void parseFullMoveClock(String part) {
  }

  private static boolean isParsable(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  public static class Builder {
    //Required parameters - required in public constructor
    private final String fen;

    //    //Optional parameters - used for test purposes and deepCopy
//    //Initialized to default values
//    //at the start of the game, all sides have the right to castle
//    private final boolean[] castlingRights = new boolean[]{true, true, true, true};
//    private boolean whiteToMove = true;
    public Builder(String fen) {
      if (fen == null) {
        throw new IllegalArgumentException("Unable to create model builder with null fenString");
      }
      this.fen = fen;
    }

//    /**
//     * Package-private setters for testing pruposes
//     */
//    Builder setWhiteToMove(boolean whiteToMove) {
//      this.whiteToMove = whiteToMove;
//      return this;
//    }

  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public void makeMove(Move m) throws IllegalStateException {

  }

  @Override
  public void startGame() {

  }

  @Override
  public boolean canMakeMove(Move m) {
    return false;
  }

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public GameResultStatus getFinalGameStatus() {
    return null;
  }

  @Override
  public boolean getWhiteToMove() {
    return this.whiteToMove;
  }

  @Override
  public ChessModel getDeepCopy() {
    return null;
  }

  @Override
  public void addListener(ModelListener listener) {

  }

  @Override
  public String getFenString() {
    return null;
  }

  @Override
  public Set<Move> getLegalMoves(PlayerColor c) {
    return null;
  }

  @Override
  public Set<RowColPair> getColorTargetSquares(PlayerColor c) {
    return null;
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position) {
    return null;
  }

  @Override
  public Optional<PieceType> getPieceTypeAt(RowColPair pair) {
    return Optional.empty();
  }
}
