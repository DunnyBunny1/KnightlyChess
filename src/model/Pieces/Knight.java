package model.Pieces;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import model.Move;
import model.Piece;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Knight extends Piece {
  public Knight(boolean isWhite) {
    super(isWhite);
  }

  private static final int[][] offsets = {
          {-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}
  };

  @Override
  public Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<Move> pseudoLegalMoves = new HashSet<>();
    for (int[] offset : offsets) {
      RowColPair candidatePosition = new RowColPair(
              position.getRow() + offset[0], position.getCol() + offset[1]);
      if (!model.isInBounds(candidatePosition)) {
        continue;
      }
      Optional<Piece> destinationPiece = board[candidatePosition.getRow()][candidatePosition.getCol()];
      if (destinationPiece.isPresent()) { //if we are here, the destination tile is occupied
        if (destinationPiece.get().getIsWhite() == isWhite) {  //if it a friendly piece, we cannot capture it.
          continue;
        }
        //if we are here, the destination tile contains an enemy piece that we can capture, or
        //the destination tile is unoccupied. In any case, we can move here.
        //knight moves have no special move flags
        pseudoLegalMoves.add(new Move(position, candidatePosition, Move.MoveFlag.NONE));
      }
    }
    return pseudoLegalMoves;
  }

  @Override
  public PieceType getType() {
    return PieceType.KNIGHT;
  }
}
