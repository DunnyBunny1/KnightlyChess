package model.Pieces;

import java.util.Set;

import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Bishop extends SlidingPiece {
  public Bishop(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    return super.getDirectionalTargetSquares(position, model, DirectionType.DIAGONAL);
  }

  @Override
  public PieceType getType() {
    return PieceType.BISHOP;
  }
}
