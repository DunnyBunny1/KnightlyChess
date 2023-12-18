package model;

/**
 * Represents the different directions relative to a given position in a game of chess
 * Each position on a chess board may or may not have an in-bounds position relative to
 * it in any given direction.
 */
public enum Direction {
  LEFT(0, -1),
  RIGHT(0, 1),
  UP(-1, 0),
  DOWN(1, 0),
  UP_LEFT(-1, -1),
  UP_RIGHT(-1, 1),
  LEFT_DOWN(1, -1),
  RIGHT_DOWN(1, 1);

  private final int rankOffset;
  private final int fileOffset;

  /**
   * Constructs a Direction with the specified row and column offsets.
   *
   * @param rankOffset The offset in the row direction.
   * @param fileOffset The offset in the column direction.
   */
  private Direction(int rankOffset, int fileOffset) {
    this.rankOffset = rankOffset;
    this.fileOffset = fileOffset;
  }

  public int getRankOffset() {
    return this.rankOffset;
  }

  public int getFileOffset() {
    return this.fileOffset;
  }
}