package model;

import java.util.Objects;

public final class Move {
  private final RowColPair src;
  private final RowColPair dest;

  public Move(RowColPair src, RowColPair dest) {
    this.src = src;
    this.dest = dest;
  }

  public RowColPair getSourcePosition() {
    return new RowColPair(src.getRow(), src.getCol());
  }

  public RowColPair getDestinationPosition() {
    return new RowColPair(dest.getRow(), dest.getCol());
  }

  @Override
  public String toString() {
    //TODO: Implemement
    return "unimplemented toString()";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Move otherMove) {
      return this.src.equals(otherMove.src) && this.dest.equals(otherMove.dest);
    }
    return false;
  }

  @Override
  public int hashCode(){
    return Objects.hash(src,dest);
  }
}
