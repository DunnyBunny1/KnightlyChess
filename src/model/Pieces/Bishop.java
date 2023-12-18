package model.Pieces;

import java.util.Set;

import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public class Bishop extends SlidingPiece {
  public Bishop(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    return super.getDiagonalTargetSquares(position,model);
  }

  @Override
  public PieceType getType() {
    return PieceType.BISHOP;
  }
}
