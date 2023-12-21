package model;

import java.util.Objects;
import java.util.Optional;

/**
 * Constructs an immutable move, none of its fields are null (invariant)
 */
public final class Move {
  private final RowColPair src;
  private final RowColPair dest;
  private final MoveFlag flag;
  public enum MoveFlag {
    PAWN_PROMOTION_TO_KNIGHT,
    PAWN_PROMOTION_TO_BISHOP,
    PAWN_PROMOTION_TO_ROOK,
    PAWN_PROMOTION_TO_QUEEN,
    DOUBLE_PAWN_PUSH,
    EN_PASSANT,
    CASTLE_KINGSIDE,
    CASTLE_QUEENSIDE,
    KING_MOVE,
    ROOK_MOVE,
    NONE;
  }

  /**
   * Constructs a move with the given source and destination positions and the given flag
   * INVARIANT: flag, src, and dest are never null
   * @implNote For castling moves, the destination is the position where the king will end up
   * @param src the source position where the piece making the move is coming from
   * @param dest the destination position where the source piece making the move will end up
   * @param flag the flag associated with the move
   * @throws IllegalArgumentException if src, dest, or flag is null
   */
  public Move(RowColPair src, RowColPair dest, MoveFlag flag) {
    if (src == null || dest == null || flag == null) {
      throw new IllegalArgumentException("Unable to construct move with null source tile, " +
              "null destination tile, or null move flag");
    }
    this.src = src;
    this.dest = dest;
    this.flag = flag;
  }
  public RowColPair getSourcePosition() {
    return new RowColPair(src.getRow(), src.getCol());
  }

  public RowColPair getDestinationPosition() {
    return new RowColPair(dest.getRow(), dest.getCol());
  }

  public MoveFlag getFlag() {
    return this.flag;
  }

  @Override
  public String toString() {
    return String.format("Move from %s to %s with flag %s", src, dest, flag);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Move otherMove) {
      return this.src.equals(otherMove.src) &&
              this.dest.equals(otherMove.dest) &&
              this.flag == otherMove.flag;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(src, dest,flag);
  }
}
