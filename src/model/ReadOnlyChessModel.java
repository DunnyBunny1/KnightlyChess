package model;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a Chess Model that can only be queried. Read-only chess models can be queried for
 * game logic and game state-related information for chess games.
 */
public interface ReadOnlyChessModel {
  /**
   * Gets the piece type at the given position in the board, or returns the empty optional if the
   * position is unoccupied.
   *
   * @param pair the 0-indexed row and column in the board position
   * @return an Optional containing either a Piece type or nothing.
   * @throws IllegalArgumentException if the position is out of bounds
   */
  Optional<PieceType> getPieceTypeAt(RowColPair pair);

  /**
   * Returns true if the given move can legally be made on the given board, false otherwise
   *
   * @param m the move to be checked
   * @return true if the move can be made, false otherwise
   * @throws IllegalStateException if either position for the move
   *                               is not in bounds.
   */
  boolean canMakeMove(Move m);

  /**
   * Returns true if the game is in progress, or false if the game is over
   *
   * @return true if the current turn has at least one legal move, false otherwise.
   * @throws IllegalStateException if the game has not started yet
   */
  boolean isGameOver();

  /**
   * Gets the resulting game status for games that are over. Can be either a win by...
   * checkmate or resignation, or a draw by...  agreement, insufficient material, or stalemate.
   *
   * @return the resulting game status, expressed as a type ofGameResultStatus
   * @throws IllegalStateException if the game is still in progress, or has not yet started.
   */
  GameResultStatus getFinalGameStatus();

  /**
   * Gets the current turn for the game.
   *
   * @return true if it is white's turn, false if it black's turn.
   * @throws IllegalStateException if the game has not yet started or has already ended.
   **/
  boolean getWhiteToMove();

  /**
   * Gets a deep copy of the given model, including the game board and all related game logic.
   *
   * @return a new ChessModel that references a different model with the exact same attributes
   */
  ChessModel getDeepCopy();

  /**
   * Registers the ModelListener as listener to this model, so that it can be notified whenever the
   * model updates itself.
   *
   * @param listener the listener to be registered.
   */
  void addListener(ModelListener listener);

  String getFenString();

  Set<Move> getLegalMoves(PlayerColor c);

  Set<RowColPair> getColorTargetSquares(PlayerColor c);

  Set<RowColPair> getTargetSquares(RowColPair position);
}
