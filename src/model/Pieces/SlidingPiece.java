package model.Pieces;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import model.Direction;
import model.Piece;
import model.ReadOnlyChessModel;
import model.RowColPair;

abstract class SlidingPiece extends Piece {

  private static Set<Direction> diagonalDirections = Set.of(Direction.UP_LEFT, Direction.UP_RIGHT,
          Direction.LEFT_DOWN, Direction.RIGHT_DOWN);
  private static Set<Direction> horizontalDirections = Set.of(Direction.RIGHT, Direction.LEFT);
  private static Set<Direction> verticalDirections = Set.of(Direction.UP, Direction.DOWN);

  protected SlidingPiece(boolean isWhite) {
    super(isWhite);
  }


  protected Set<RowColPair> getDiagonalTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    Set<Direction> validDirections = diagonalDirections;
    return targetSquares;
  }

  protected Set<RowColPair> getVerticalTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    Set<Direction> validDirections = verticalDirections;
    return targetSquares;

  }

  protected Set<RowColPair> getHorizontalTargetSquares(RowColPair position, ReadOnlyChessModel model) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    Set<Direction> validDirections = horizontalDirections;
    Optional<Piece>[][] board = model.getBoardCopy();

    for (Direction direction : validDirections) {
      RowColPair candidate = new RowColPair(position.getRow() + direction.getRankOffset(),
              position.getCol() + direction.getFileOffset());
      if (model.isInBounds(candidate)) {
        Optional<Piece> piece = board[candidate.getRow()][candidate.getCol()];
        if (piece.isEmpty()) { //if we are here, we are making a move to an empty tile
          targetSquares.add(candidate);
        } else if (piece.get().getIsWhite() != this.isWhite) { //we can make a capture move
          targetSquares.add(candidate);
          //TODO: if we are here or in the else, a piece stands in our way, so we need to add in
          // a new inner loop and then continue the outer loop
        } else{

        }
      }
    }
    return targetSquares;

  }
}
