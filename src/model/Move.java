package model;

import java.util.Objects;
import java.util.Optional;

public final class Move {
  private final RowColPair src;
  private final RowColPair dest;

  private final PieceType pawnPromotionPieceType;
//  private final MoveFlag flag;
//  private final RowColPair enPassantCapturedPiece;
//TODO: Uncomment above ^
  public enum MoveFlag {
    PAWN_PROMOTION,
    DOUBLE_PAWN_PUSH,
    EN_PASSANT,
    CASTLE,
    KING_MOVE,
    ROOK_MOVE;
  }

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

  public class Builder {
    private final RowColPair src;
    private final RowColPair dest;

    private PieceType pawnPromotionPieceType;
    private MoveFlag flag;
    private RowColPair enPassantCapturedPiece;

    public Builder(RowColPair src, RowColPair dest) {
      this.src = src;
      this.dest = dest;
    }
    public Builder setFlag(MoveFlag flag) {
      if(flag == null){
        throw new IllegalArgumentException("Unable to construct move with null move flag");
      }
      this.flag = flag;
      return this;
    }

    public Builder setPawnPromotionPieceType(PieceType type) {
      if(type == null || !ChessModel.pawnPromotionPieceTypes.contains(type)){
        throw new IllegalArgumentException("Unable to construct move for null or illegal" +
                "pawn promotion piece type");
      }
      this.pawnPromotionPieceType = pawnPromotionPieceType;
      return this;
    }

    public Builder setEnPassantCapturedPiece(){
      return null ;
    }

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

//  public Optional<RowColPair> getEnPassantCapturedPiece() {
//    return Optional.ofNullable(this.enPassantCapturedPiece);
//  }
//
//  public void setFlag(MoveFlag flag) {
//    this.flag = flag;
//  }
//
//  public Optional<MoveFlag> getFlag() {
//    return Optional.ofNullable(this.flag);
//  }


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
