package model.Pieces;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import model.Piece;
import model.PieceType;
import model.ReadOnlyChessModel;
import model.RowColPair;

public final class Pawn extends Piece {
  public Pawn(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    //Pawns can move forward, capture diagonally, and do en passant
    targetSquares.addAll(getForwardTargetSquares(position, model));
    targetSquares.addAll(getDiagonalCaptureTargets(position, model));
    targetSquares.addAll(getEnPassantSquares(position, model));
    return targetSquares;
  }

  private Set<RowColPair> getForwardTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<RowColPair> targetSquares = new HashSet<>();
    //TODO: Implement logic
    return targetSquares;
  }

  private Set<RowColPair> getDiagonalCaptureTargets(RowColPair position, ReadOnlyChessModel model) {
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<RowColPair> targetSquares = new HashSet<>();
    //TODO: Implement logic
    return targetSquares;
  }

  private Set<RowColPair> getEnPassantSquares(RowColPair position, ReadOnlyChessModel model) {
    //if we are here, we are safe to retrieve the piece and board
    Optional<Piece>[][] board = model.getBoardCopy();
    Set<RowColPair> targetSquares = new HashSet<>();
    //TODO: Implement logic
    return targetSquares;
  }

  @Override
  public PieceType getType() {
    return PieceType.PAWN;
  }
}
