package model;

import java.util.Objects;

/**
 * Immutable
 */
public final class RowColPair {
  private final int row;
  private final int col;

  public RowColPair(int r, int c) {
    this.row = r;
    this.col = c;
  }

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (other instanceof RowColPair otherPair) {
      return otherPair.row == this.row && otherPair.col == this.col;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
}
