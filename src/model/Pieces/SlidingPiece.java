package model.Pieces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import model.ChessModel;
import model.Direction;
import model.Move;
import model.Piece;
import model.ReadOnlyChessModel;
import model.RowColPair;

/**
 * sliding
 */
abstract class SlidingPiece extends Piece {

  protected enum DirectionType {
    DIAGONAL,
    HORIZONTAL,
    VERTICAL;
  }

  protected static final Set<Direction> diagonalDirections = Set.of(Direction.UP_LEFT, Direction.UP_RIGHT,
          Direction.LEFT_DOWN, Direction.RIGHT_DOWN);
  protected static final Set<Direction> horizontalDirections = Set.of(Direction.RIGHT, Direction.LEFT);
  protected static final Set<Direction> verticalDirections = Set.of(Direction.UP, Direction.DOWN);
  private static final Map<DirectionType, Set<Direction>> validDirectionMap;

  static {
    validDirectionMap = new HashMap<>();
    validDirectionMap.put(DirectionType.HORIZONTAL, horizontalDirections);
    validDirectionMap.put(DirectionType.VERTICAL, verticalDirections);
    validDirectionMap.put(DirectionType.DIAGONAL, diagonalDirections);

  }

  protected SlidingPiece(boolean isWhite) {
    super(isWhite);
  }

  protected final Set<Move> getSlidingPseudoLegalMoves(
          RowColPair position, ReadOnlyChessModel model, DirectionType... directionTypes
  ) {
    Set<Move> pseudoLegalMoves = new HashSet<>();
    for (DirectionType directionType : directionTypes) {
      Set<RowColPair> directionalPseudoLegalTargetSquares =
              getDirectionalPseudoLegalTargetSquares(position, model, directionType);
      for (RowColPair target : directionalPseudoLegalTargetSquares) {
        Move.MoveFlag flag = getMoveFlag(position, target, model);
        Move move = new Move(position, target, flag);
        pseudoLegalMoves.add(move);
      }
    }
    return pseudoLegalMoves;
  }

  protected abstract Move.MoveFlag getMoveFlag(RowColPair position, RowColPair destination, ReadOnlyChessModel model);

  private Set<RowColPair> getDirectionalPseudoLegalTargetSquares(
          RowColPair position, ReadOnlyChessModel model, DirectionType directionType) {
    checkModelAndPositionValidity(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    Set<Direction> validDirections = validDirectionMap.get(directionType);
    Optional<Piece>[][] board = model.getBoardCopy();
    for (Direction direction : validDirections) {
      //n is the target square number in a given direction. The target square can be up to
      //NUM_ranks -1 = 7 squares away from the source square in a given direction
      for (int n = 0; n < ChessModel.NUM_RANKS - 1; n++) {
        RowColPair candidate = new RowColPair(position.getRow() + direction.getRankOffset() * (n + 1),
                position.getCol() + direction.getFileOffset() * (n + 1));
        if (!model.isInBounds(candidate)) { //if we go over the edge of the board, look in a new direction
          break;
        }
        //Get the current piece at the target square
        Optional<Piece> piece = board[candidate.getRow()][candidate.getCol()];
        //If we are blocked by a friendly piece, there are no possible moves in current direction
        if (piece.isPresent() && piece.get().getIsWhite() == this.isWhite) {
          break;
        } //if we are here, the target square is either empty or occupied by an enemy
        targetSquares.add(candidate); //the move either be a capture or a move to an empty tile
        if (piece.isPresent()) { //if we are here, the present piece does not match our color
          //This means we just captured an enemy piece
          break; //After capturing an enemy piece, there are no more possible moves in a direction
        }
      }
    }
    return targetSquares;
  }
}
