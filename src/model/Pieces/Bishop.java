package model.Pieces;

import java.util.Set;

import model.Move;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Bishop extends SlidingPiece {
  public Bishop(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model) {
    //bishops can only move diagonally
    return super.getSlidingPseudoLegalMoves(position, model, DirectionType.DIAGONAL);
  }

  @Override
  protected Move.MoveFlag getMoveFlag(RowColPair position, RowColPair destination, ReadOnlyChessModel model) {
    return Move.MoveFlag.NONE; // bishops have no specialty move flags
  }

  @Override
  public PieceType getType() {
    return PieceType.BISHOP;
  }
}
