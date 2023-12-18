package model;

import java.util.Objects;
import java.util.Optional;

public final class Move {
  private final RowColPair src;
  private final RowColPair dest;
  private final PieceType pawnPromotionPieceType;

  public Move(RowColPair src, RowColPair dest) {
    this.src = src;
    this.dest = dest;
    this.pawnPromotionPieceType = null;
  }

  public Move(RowColPair src, RowColPair dest, PieceType pawnPromotionPieceType) {
    this.src = src;
    this.dest = dest;
    this.pawnPromotionPieceType = pawnPromotionPieceType;
  }

  public RowColPair getSourcePosition() {
    return new RowColPair(src.getRow(), src.getCol());
  }

  public RowColPair getDestinationPosition() {
    return new RowColPair(dest.getRow(), dest.getCol());
  }

  public Optional<PieceType> getPawnPromotionPieceType() {
    return Optional.ofNullable(pawnPromotionPieceType);
  }

  @Override
  public String toString() {
    String pawnPromotionString = pawnPromotionPieceType == null ? "" :
            "with pawn promotion piece type" + pawnPromotionPieceType.getLowercasedPieceID();
    return String.format("Move from %s to %s %s", src, dest, pawnPromotionString);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Move otherMove) {
      return this.src.equals(otherMove.src) && this.dest.equals(otherMove.dest) && Objects.equals(
              Optional.ofNullable(pawnPromotionPieceType), otherMove.getPawnPromotionPieceType());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(src, dest);
  }
}
