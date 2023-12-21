package model.Pieces;

import java.util.Set;

import model.Move;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Queen extends AbstractSlidingPiece {
  public Queen(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model) {
    //queens can move both horizontally, vertically, and diagonally
    return super.getSlidingPseudoLegalMoves(
            position, model, DirectionType.DIAGONAL,DirectionType.VERTICAL,DirectionType.HORIZONTAL);
  }

  @Override
  protected Move.MoveFlag getMoveFlag(RowColPair position, RowColPair destination, ReadOnlyChessModel model) {
    return Move.MoveFlag.NONE; // queens have no specialty move flags
  }

  @Override
  public PieceType getType()  {
    return PieceType.QUEEN;
  }
}
