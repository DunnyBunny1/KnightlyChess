package model;

/**
 * Represents a Chess Model variant allowing pseudo legal moves without strict validation.
 * This model permits moves beyond strict legality, such as moving into check or moving a pinned piece.
 */
public interface PermissiveChessModel extends ReadOnlyChessModel {
  /**
   * Makes a pseudo legal move on the given model. Pseudo legal moves are a superset of legal moves
   *
   * @param m the move to be made
   */
  void makePseudoLegalMove(Move m);

}
