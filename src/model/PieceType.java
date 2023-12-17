package model;

public enum PieceType {
  PAWN("p"),
  KNIGHT("n"),
  BISHOP("b"),
  ROOK("r"),
  QUEEN("q"),
  KING("k");

  private final String pieceID;

  private PieceType(String pieceID) {
    this.pieceID = pieceID;
  }
}