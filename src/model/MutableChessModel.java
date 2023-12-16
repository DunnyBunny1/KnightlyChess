package model;

/**
 * Represents a Chess Model that can be manipulated. Mutable chess models contain
 * the domain logic, about the current game state, for a game of chess,
 * and this game logic can be updated. Mutatable chess models can be queried
 * for game-logic related information or mutated to cause some change in game state.
 */
public interface MutableChessModel extends ReadOnlyChessModel {
  /**
   * Applies the given move to the given model
   *
   * @param m the move to be applied
   * @throws IllegalStateException    if the move cannot
   *                                  be made on the given board, due to pins
   * @throws IllegalArgumentException if the source position
   *                                  or destination position are not in bounds
   */
  void makeMove(Move m) throws IllegalStateException;

  /**
   * Sets the game to the initial position and allow for moves to be
   * made.
   *
   * @throws IllegalStateException if the game has already been started
   *                               or the game is over
   */
  void startGame();

  /*
   * Resigns the game for the given color, causing the opposite player color to receive the win.
   *
   * @param color the color that would like to resign
   * @throws IllegalStateException if the game has not yet started, or if the game is already over.

  void makeResignationMove(PlayerColor color);

  /*
  /**
   * Offers a draw for the given color. If both colors offer a draw on the same move, then the
   * game will be over and will end in a draw by agreement
   * @param color the color that is being offered a draw.

  void offerDraw(PlayerColor color);

   */
}
