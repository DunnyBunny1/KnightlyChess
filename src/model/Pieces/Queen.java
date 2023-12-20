package model.Pieces;

import java.util.HashSet;
import java.util.Set;

import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Queen extends SlidingPiece {
  public Queen(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    Set<RowColPair> targetSquares = new HashSet<>();
    targetSquares.addAll(super.getDirectionalTargetSquares(position, model, DirectionType.HORIZONTAL));
    targetSquares.addAll(super.getDirectionalTargetSquares(position, model, DirectionType.VERTICAL));
    targetSquares.addAll(super.getDirectionalTargetSquares(position, model, DirectionType.DIAGONAL));
    return targetSquares;
  }

  @Override
  public PieceType getType() {
    return PieceType.QUEEN;
  }
}
