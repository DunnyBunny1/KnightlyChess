package model;
//TODO: Write javadoc
public enum PlayerColor {
  BLACK,
  WHITE;

  public PlayerColor getOpposite() {
    if (this != BLACK && this != WHITE) {
      throw new IllegalArgumentException(String.format("Unable to generate opposite color for color " +
              "%s", this));
    }
    return this == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
  }
}
