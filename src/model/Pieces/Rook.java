package model.Pieces;

import java.util.HashSet;
import java.util.Set;

import model.Move;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Rook extends SlidingPiece {
  public Rook(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model) {
    //rooks can move both horizontally and vertically
    return super.getSlidingPseudoLegalMoves(position, model, DirectionType.HORIZONTAL,DirectionType.VERTICAL);
  }

  @Override
  protected Move.MoveFlag getMoveFlag(RowColPair position, RowColPair destination, ReadOnlyChessModel model) {
    return Move.MoveFlag.ROOK_MOVE; // Rooks have a specialty move flag so that we can keep track of if
    //have moved or not for castling privileges
  }

  @Override
  public PieceType getType() {
    return PieceType.ROOK;
  }
}
