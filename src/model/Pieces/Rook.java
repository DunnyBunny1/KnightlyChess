package model.Pieces;

import java.util.HashSet;
import java.util.Set;

import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public class Rook extends SlidingPiece {
  public Rook(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    Set<RowColPair> targetSquares = new HashSet<>();
    targetSquares.addAll(super.getHorizontalTargetSquares(position, model));
    targetSquares.addAll(super.getVerticalTargetSquares(position, model));
    return targetSquares;
  }

  @Override
  public PieceType getType() {
    return PieceType.ROOK;
  }
}
