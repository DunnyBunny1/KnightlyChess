package model;

public enum PieceType {
  PAWN('p'),
  KNIGHT('n'),
  BISHOP('b'),
  ROOK('r'),
  QUEEN('q'),
  KING('k');

  private final char pieceID;

  private PieceType(char pieceID) {
    this.pieceID = pieceID;
  }

  public char getLowercasedPieceID() {
    return this.pieceID;
  }
}