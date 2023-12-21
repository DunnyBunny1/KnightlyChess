package model.Pieces;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import model.Move;
import model.Piece;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Pawn extends Piece {
  public Pawn(boolean isWhite) {
    super(isWhite);
  }

  public static final Set<Move.MoveFlag> promotionFlags = Set.of(
          Move.MoveFlag.PAWN_PROMOTION_TO_KNIGHT,
          Move.MoveFlag.PAWN_PROMOTION_TO_BISHOP,
          Move.MoveFlag.PAWN_PROMOTION_TO_ROOK,
          Move.MoveFlag.PAWN_PROMOTION_TO_QUEEN
  );

  @Override
  public Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);

    Set<Move> pseudoLegalMoves = new HashSet<>();
    //Pawns can move forward, capture diagonally, and do en passant
    pseudoLegalMoves.addAll(getNonPromotionSingleForwardMoves(position, model));
    pseudoLegalMoves.addAll(getDoubleForwardMoves(position, model));
    pseudoLegalMoves.addAll(getPromotionMoves(position, model));
    pseudoLegalMoves.addAll(getNonPromotionDiagonalCaptures(position, model));
    pseudoLegalMoves.addAll(getEnPassantMoves(position, model));
    return pseudoLegalMoves;
  }

  private Set<Move> getNonPromotionSingleForwardMoves(RowColPair position, ReadOnlyChessModel model) {
    Set<Move> pseudoLegalMoves = new HashSet<>(); //We want to ignore any moves that are promotions
    if (isRankBeforePromotion(position)) {
      return pseudoLegalMoves;
    }
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    //Add all the non-promotion single forward moves
    //Pawns can only move to empty squares directly in front of them
    int rankOffset = this.isWhite ? -1 : 1;
    RowColPair candidatePosition = new RowColPair(position.getRow() + rankOffset, position.getCol());
    if (model.isInBounds(candidatePosition) && board[candidatePosition.getRow()][candidatePosition.getCol()].isEmpty()) {
      pseudoLegalMoves.add(new Move(position, candidatePosition, Move.MoveFlag.NONE));
    }
    return pseudoLegalMoves;
  }

  private Set<Move> getDoubleForwardMoves(RowColPair position, ReadOnlyChessModel model) {
    Set<Move> pseudoLegalMoves = new HashSet<>();
    int startingRank = this.isWhite ? 6 : 1;
    if (position.getRow() != startingRank) { //pawns can only double move from their starting rank
      return pseudoLegalMoves;
    }
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    //Add all the double forward moves, pawns can only move to empty squares directly in front of them
    int rankOffset = this.isWhite ? -2 : 2;
    RowColPair candidatePosition = new RowColPair(position.getRow() + rankOffset, position.getCol());
    if (model.isInBounds(candidatePosition) && board[candidatePosition.getRow()][candidatePosition.getCol()].isEmpty()) {
      pseudoLegalMoves.add(new Move(position, candidatePosition, Move.MoveFlag.DOUBLE_PAWN_PUSH));
    }
    return pseudoLegalMoves;
  }

  private Set<Move> getNonPromotionDiagonalCaptures(RowColPair position, ReadOnlyChessModel model) {
    Set<Move> pseudoLegalMoves = new HashSet<>(); //We want to ignore any moves that are promotions
    if (isRankBeforePromotion(position)) {
      return pseudoLegalMoves;
    }
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Piece piece = board[position.getRow()][position.getCol()].get();
    //pawns can only move diagonally if they are capturing an enemy piece
    //get the right diagonal and left diagonal, and check if they are occupied by an enemy piece
    int rightFileOffset = this.isWhite ? 1 : -1;
    int leftFileOffset = this.isWhite ? -1 : 1;
    int rankOffset = this.isWhite ? -1 : 1;
    RowColPair rightDiagonal = new RowColPair(position.getRow() + rankOffset, position.getCol() + rightFileOffset);
    if (isOccupiedByEnemyPiece(model, rightDiagonal)) {
      pseudoLegalMoves.add(new Move(position, rightDiagonal, Move.MoveFlag.NONE));
    }
    RowColPair leftDiagonal = new RowColPair(position.getRow() + rankOffset, position.getCol() + leftFileOffset);
    if (isOccupiedByEnemyPiece(model, leftDiagonal)) {
      pseudoLegalMoves.add(new Move(position, leftDiagonal, Move.MoveFlag.NONE));
    }
    return pseudoLegalMoves;
  }

  private Set<Move> getEnPassantMoves(RowColPair position, ReadOnlyChessModel model) {
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<Move> pseudoLegalMoves = new HashSet<>();
    Piece piece = board[position.getRow()][position.getCol()].get();
    //only white pawns on rank 3 and black pawns on rank 4 can en passant
    int enPassantRank = this.isWhite ? 3 : 4;
    if (position.getRow() != enPassantRank) { //ensure we are on the correct en passant rank
      return pseudoLegalMoves;
    }
    //Find all the en-passant moves that are possible on our given model
    //check if the model en passant target square is present
    if (model.getEnPassantTarget().isPresent()) {
      RowColPair enPassantTarget = model.getEnPassantTarget().get();
      //check if the en passant target square is diagonally adjacent to the pawn;
      //if the column (file) distance is one, and we are on the proper en-passant rank, we can en passant
      if (Math.abs(enPassantTarget.getCol() - position.getCol()) == 1) {
        //check if the en passant target square is empty
        if (board[enPassantTarget.getRow()][enPassantTarget.getCol()].isEmpty()) {
          //if we are here, we have a valid en passant move
          pseudoLegalMoves.add(new Move(position, enPassantTarget, Move.MoveFlag.EN_PASSANT));
        }
      }
    }
    return pseudoLegalMoves;
  }

  private Set<Move> getPromotionMoves(RowColPair position, ReadOnlyChessModel model) {
    Set<Move> pseudoLegalMoves = new HashSet<>(); //We want to ignore any moves that are not promotions
    if (!isRankBeforePromotion(position)) {
      return pseudoLegalMoves;
    }
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Piece piece = board[position.getRow()][position.getCol()].get();
    //Add all the diagonal capture promotions to the set of pseudo-legal moves
    //get the right diagonal and left diagonal, and check if they are occupied by an enemy piece
    int rightFileOffset = this.isWhite ? 1 : -1;
    int leftFileOffset = this.isWhite ? -1 : 1;
    int rankOffset = this.isWhite ? -1 : 1;
    RowColPair rightDiagonal = new RowColPair(position.getRow() + rankOffset, position.getCol() + rightFileOffset);
    if (isOccupiedByEnemyPiece(model, rightDiagonal)) {
      for (Move.MoveFlag flag : promotionFlags) { //Add one move for each promotion type
        pseudoLegalMoves.add(new Move(position, rightDiagonal, flag));
      }
    }
    RowColPair leftDiagonal = new RowColPair(position.getRow() + rankOffset, position.getCol() + leftFileOffset);
    if (isOccupiedByEnemyPiece(model, leftDiagonal)) {
      for (Move.MoveFlag flag : promotionFlags) { //Add one move for each promotion type
        pseudoLegalMoves.add(new Move(position, rightDiagonal, flag));
      }
    }
    //Add all the single forward non-capture promotions to the set of pseudo-legal moves
    RowColPair candidatePosition = new RowColPair(position.getRow() + rankOffset, position.getCol());
    if (model.isInBounds(candidatePosition) && board[candidatePosition.getRow()][candidatePosition.getCol()].isEmpty()) {
      for (Move.MoveFlag flag : promotionFlags) { //Add one move for each promotion type
        pseudoLegalMoves.add(new Move(position, candidatePosition, flag));
      }
    }
    return pseudoLegalMoves;
  }

  @Override
  public PieceType getType() {
    return PieceType.PAWN;
  }

  private boolean isOccupiedByEnemyPiece(ReadOnlyChessModel model, RowColPair target) {
    Optional<Piece>[][] board = model.getBoardCopy();
    if (model.isInBounds(target) && board[target.getRow()][target.getCol()].isPresent()) {
      Piece leftDiagonalPiece = board[target.getRow()][target.getCol()].get();
      return leftDiagonalPiece.getIsWhite() != this.isWhite;
    }
    return false;
  }

  private boolean isRankBeforePromotion(RowColPair position) {
    int rankBeforePromotion = this.isWhite ? 1 : 6;
    return rankBeforePromotion == position.getRow();
  }
}
