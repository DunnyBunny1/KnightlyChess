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
   * @return an Optional containing either a Pieces type or nothing.
   * @throws IllegalArgumentException if the position is null or out of bounds
   */
  Optional<PieceType> getPieceTypeAt(RowColPair pair);

  /**
   * Returns true if the given move can legally be made on the given board, false otherwise
   *
   * @param m the move to be checked
   * @return true if the move can be made, false otherwise
   * @throws IllegalArgumentException if the move or either position of the move is null or
   *                                  not in bounds.
   * @throws IllegalStateException    if the game is over or not in progress.
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
   * <ol>
   *   <li>
   *     checkmate
   *   </li>
   *   <li>
   *     resignation
   *   </li>
   * </ol>
   * or a draw by...
   * <ol>
   *   <li>
   *     agreement
   *   </li>
   *   <li>
   *     insufficient material
   *   </li>
   *   <li>
   *     stalemate
   *   </li>
   * </ol>,
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
   * Gets a strict deep copy of the given model, including the game board and all related game
   * logic. A strict deep copy defines the exact same logic for determining move legality
   * as the current model implementation.
   *
   * @return a new ChessModel that references a different model with the exact same attributes.
   * This deep copy is also mutable - so it can be mutated by applying moves to it.
   */
  MutableChessModel getStrictDeepCopy();

  /**
   * Returns a permissive deep copy of the given model, including the game board and all related game
   * state logic. A permissive deep copy allows pseudo legal moves to be made on the model.
   *
   * @return
   */
  PermissiveChessModel getPermissibleDeepCopy();

  /**
   * Registers the ModelListener as listener to this model, so that it can be notified whenever the
   * model updates itself.
   *
   * @param listener the listener to be registered.
   * @throws IllegalArgumentException if the listener is null
   * @throws IllegalStateException    if the game has already started or is already over.
   */
  void addListener(ModelListener listener);

  /**
   * Returns a Set of all the legal moves for the given player color.
   * Legal moves are defined by each specific read only chess model implementation.
   * For example, a permissive chess model may return a set of pseudo-legal moves, while a strict
   * chess model may return a set of strictly legal moves.
   *
   * @param c the player color for which to get the legal moves from.
   * @return the collection of all the legal moves, as a set.
   * @throws IllegalStateException    if the game is over or has not yet started
   * @throws IllegalArgumentException if the color is not either black or white.
   */
  Set<Move> getLegalMoves(PlayerColor c);

  /**
   * Gets a set of all the target squares for the given player color
   *
   * @param c the player color for which to get the target squares for
   * @return the collection of all target squares, expressed as RowColPair's
   * @throws IllegalStateException    if the game is over or has not yet started
   * @throws IllegalArgumentException if the color is not either black or white
   */
  Set<RowColPair> getColorTargetSquares(PlayerColor c);

  /**
   * If the position is occupied, gets the set of all the target squares for the piece at the given
   * position. Otherwise, returns the empty optional
   *
   * @param position the row col pair on the baord from which to retrieve the set of target squares
   * @return an optional of a set of target squares, with each target square expressed as a
   * RowColPair
   * @throws IllegalStateException    if the game is over or has not yet started
   * @throws IllegalArgumentException if the given position is not in bounds
   */
  Optional<Set<RowColPair>> getTargetSquares(RowColPair position);

  /**
   * Returns the RowColPair whose row and column correspond to the rank and file on the board where
   * the King of the given color is on the board.
   *
   * @param c the color to retrieve the king square from
   * @return the 0-indexed row col pair of the king square.
   * @throws IllegalStateException    if the game has not yet started
   * @throws IllegalArgumentException if the color is not either black or white
   * @throws IllegalStateException    if the king square is not found
   */
  RowColPair getKingSquare(PlayerColor c);

  /**
   * Returns the letter square combination for the given row col pair.
   *
   * @param position the 0-based indexed row col pair to convert to a letter square combo
   * @return the letter square combination, for example (2,7) -> b1
   * @throws IllegalArgumentException if the position is invalid
   */
  String getLetterSquareCombination(RowColPair position);

  /**
   * Returns a copy of the model's board grid. Each position on the grid has an Optional<Pieces>,
   * unoccupied positions contain the empty optional, and occupied ones contain optionals containing
   * their respective piece
   *
   * @return a deep copy of the model's board grid.
   */
  Optional<Piece>[][] getBoardCopy();

  /**
   * Returns true if the position is non-null and the position's row and column are a legal rank
   * and file, respectively
   *
   * @param pair the RowColPair position to be inspected.
   * @return true if the position is non-null and in bounds, false otherwise.
   */
  boolean isInBounds(RowColPair pair);

  /**
   * Returns the castling privileges for the current board state, as a string. K = white kingside,
   * Q = white queenside, k = black kingside, q = black queenside. If no castling privileges exist,
   * the string '-' is returned.
   *
   * @return an immutable string representing the castling privileges for the current board state.
   */
  String getCastlingPrivileges();

  /**
   * Returns the en passant target square, if it exists. Otherwise, returns the empty optional
   *
   * @return the en passant target as a 0-indexed row col pair, or the empty optional if it does not
   * exist.
   */
  Optional<RowColPair> getEnPassantTarget();
}
