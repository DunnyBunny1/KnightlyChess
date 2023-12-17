package model;

import java.util.Set;

public abstract class Piece {
  protected final PlayerColor color;

  protected Piece(PlayerColor c) {
    if (c != PlayerColor.WHITE && c != PlayerColor.BLACK) {
      throw new IllegalArgumentException(String.format("Unable to create piece with color %s", c));
    }
    this.color = c;
  }

  public abstract Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model);

  public abstract PieceType getType();


}
