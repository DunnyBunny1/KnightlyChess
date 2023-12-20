package model.Pieces;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import model.Direction;
import model.Piece;
import model.PieceType;
import model.PlayerColor;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class King extends Piece {
  public King(boolean isWhite) {
    super(isWhite);
  }
  //since this is an unmodifiable map and RowColPairs are immutable, this is safe to be made public
  public static final Map<Character, RowColPair> fenCharToCandidateCastlingSquare;

  static {
    //Use map.of() to create an unmodifiable map
    fenCharToCandidateCastlingSquare = Map.of('K', new RowColPair(7, 6),
            'Q', new RowColPair(7, 2),
            'k', new RowColPair(0, 6),
            'q', new RowColPair(0, 2)
    );
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    //Kings can both castle and move one slot in each direction
    targetSquares.addAll(getDirectionalTargetSquares(position, model));
    targetSquares.addAll(getCastlingTargetSquares(position, model));
    return targetSquares;
  }

  private Set<RowColPair> getDirectionalTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<RowColPair> targetSquares = new HashSet<>();
    for (Direction direction : Direction.values()) {
      RowColPair candidatePosition = new RowColPair(position.getRow() + direction.getRankOffset()
              , position.getCol() + direction.getFileOffset());
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
        targetSquares.add(candidatePosition);
      }
    }
    return targetSquares;
  }

  private Set<RowColPair> getCastlingTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    Optional<Piece>[][] board = model.getBoardCopy();
    //if the king is in check, we cannot castle
    PlayerColor oppositeColor = this.isWhite ? PlayerColor.BLACK : PlayerColor.WHITE;
    if (model.getColorTargetSquares(oppositeColor).contains(position)) {
      return Collections.emptySet();
    }
    Set<RowColPair> targetSquares = new HashSet<>();
    char[] castlingPrivileges = model.getCastlingPrivileges().toCharArray();
    for (char castlingPrivilege : castlingPrivileges) {
      //if we still have castling privileges for the given privilege...
      if (fenCharToCandidateCastlingSquare.containsKey(castlingPrivilege)) {
        RowColPair candidate = fenCharToCandidateCastlingSquare.get(castlingPrivilege);
        //if we have castling privileges and a clear path to castle, we can castle
        if (hasClearPathToCastle(position, candidate, model)) {
          targetSquares.add(candidate);
        }
      }
    }
    return targetSquares;
  }

  private boolean hasClearPathToCastle(RowColPair kingPosition, RowColPair candidate, ReadOnlyChessModel model) {
    Optional<Piece>[][] board = model.getBoardCopy();
    //for castling, the rows should be the same and the files should be different
    int difference = kingPosition.getCol() - candidate.getCol();
    if (difference > 0) {//if we are here, we the king file is > the candidate file
      //we are doing a queenside castle
      //check all the position in between the king and candidate, if they are all empty,
      //we have a clear path. Otherwise, we do not have a clear path
      for (int file = kingPosition.getCol() - 1; file >= candidate.getCol(); file--) {
        if (board[candidate.getRow()][file].isPresent()) {
          return false; //if we are here, something is in our way
        }
      }
    } else { //if we are here, the candidate file is > the king file
      //we are doing a kingside castle
      //check all the position in between the king and candidate, if they are all empty,
      //we have a clear path. Otherwise, we do not have a clear path
      for (int file = kingPosition.getCol() + 1; file <= candidate.getCol(); file++) {
        if (board[candidate.getRow()][file].isPresent()) {
          return false; //if we are here, something is in our way
        }
        //We cannot castle through check, so ensure that the path is not targeted by enemy
        PlayerColor oppositeColor = this.isWhite ? PlayerColor.BLACK : PlayerColor.WHITE;
        if (model.getColorTargetSquares(oppositeColor).contains(candidate)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public PieceType getType() {
    return PieceType.KING;
  }
}
