package model;
//TODO: Write javadoc
public enum PlayerColor {
  BLACK,
  WHITE;

  public PlayerColor getOpposite(PlayerColor color) {
    if (color != BLACK && color != WHITE) {
      throw new IllegalArgumentException(String.format("Unable to generate opposite color for color " +
              "%s", color));
    }
    return color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
  }
}
