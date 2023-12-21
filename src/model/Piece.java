package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a Pieces for a game of chess. All chess pieces are immutable after creation.
 */
public abstract class Piece {
  protected final boolean isWhite;

  public Piece(boolean isWhite) {
    this.isWhite = isWhite;
  }

  /**
   * Given a model, retrieves the pseudo-legal moves - all the moves that this piece can make
   * on a given board, including moves that may be illegal. Pseudo-legal moves are a superset of all
   * the legal moves and need to be filtered to find the list of all legal moves. Pseudo legal moves
   * do not account for checks. For legal move generation, each pseudo legal move needs to be
   * verified to ensure it doesn't put its own king in check. Each move consists of this position as
   * a source with each pseudo legal target square that this piece can move to as a destination.
   *
   * @param position the position in the board that a piece of this type and color is located at
   * @param model    the chess model to query to calculate moves
   * @return the Set of pseudo legal moves, with each move expressed as a move - a RowColPair source,
   * consisting of the position parameter, and a RowColPair destination, consisting of a pseudo
   * legal target position.
   * @throws IllegalArgumentException if the model or position is invalid
   */
  public abstract Set<Move> getPseudoLegalMoves(RowColPair position, ReadOnlyChessModel model);


//  /**
//   * Gets the legal moves - all the moves consisting with given source position and destination
//   * position consisting of a destination positions that this piece can move to for the given model.
//   * All of these moves are legal - they can be made and will not put the player executing the move
//   * in check.
//   *
//   * @param position the position in the board that a piece of this type and color is located at
//   * @param model    the chess model to query to calculate moves
//   * @return the Set of legal moves, with move consisting of a source and desintaiton position
//   * @throws IllegalArgumentException if the model or position is invalid
//   */
//  protected final Set<Move> getLegalMoves(RowColPair position, ReadOnlyChessModel model) {
//    checkModelAndPositionValidity(position, model);
//    Set<Move> moves = getPseudoLegalMoves(position, model);
//    PlayerColor friendlyColor = this.isWhite ? PlayerColor.WHITE : PlayerColor.BLACK;
//    moves.removeIf(m -> {
//      PermissiveChessModel copy = model.getPermissibleDeepCopy();
//      copy.makePseudoLegalMove(m);
//      Set<RowColPair> enemyTargetSquares = copy.getColorTargetSquares(friendlyColor.getOpposite());
//      return enemyTargetSquares.contains(model.getKingSquare(friendlyColor));
//    });
//    return moves;
//  }

  /**
   * Gets the target squares - all the possible destination positions that this piece can move
   * to for the given model.
   *
   * @param position the position in the board that a piece of this type and color is located at
   * @param model    the chess model to query to calculate moves
   * @return the Set of target squares, with each target position expressed as a RowColPair
   * @throws IllegalArgumentException if the model or position is invalid
   */
  public final Set<RowColPair> getTargetSquares(RowColPair position, ReadOnlyChessModel model){
    Set<Move> legalMoves = this.getLegalMoves(position, model);
    Set<RowColPair> targetSquares = new HashSet<>();
    for(Move move : legalMoves){
      targetSquares.add(move.getDestinationPosition());
    }
    return targetSquares;
  }

  /**
   * Factory method to return the piece type of the given piece implementation
   *
   * @return the type of Pieces, expressed as a PieceType
   */
  public abstract PieceType getType();

  /**
   * Returns a boolean determining if this piece's color is white or not
   *
   * @return true if this piece's color is white, false if this piece's color is black.
   */
  public final boolean getIsWhite() {
    return this.isWhite;
  }

  /**
   * Checks that the given model and position are valid for move or target tile calculation.
   * Does nothing if the position and model are valid to calculate moves or target tiles from.
   *
   * @param position the position whose validity to verify.
   * @param model    the model whose validity to verify
   * @throws IllegalArgumentException if position is null or out of bounds, or if model is null
   * @throws IllegalArgumentException if the position in the board is empty, or does not contain a
   *                                  piece that matches this piece's color and type
   */
  protected final void checkModelAndPositionValidity(RowColPair position, ReadOnlyChessModel model) {
    if (model == null || position == null || !model.isInBounds(position)) {
      throw new IllegalArgumentException("Unable to get target squares for null model or null" +
              "or out of bounds position.");
    }
    Optional<Piece>[][] board = model.getBoardCopy();
    Optional<Piece> piece = board[position.getRow()][position.getCol()];
    if (piece.isEmpty()) {
      throw new IllegalArgumentException("Unable to get target squares for unoccupied position");
    }
    if (piece.get().getIsWhite() != this.isWhite || piece.get().getType() != this.getType()) {
      throw new IllegalArgumentException("Unable to get knight target squares for piece that does " +
              "not match type knight or does not match color");
    }
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Piece otherPiece) {
      return this.getType() == otherPiece.getType() && this.isWhite == otherPiece.getIsWhite();
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(this.isWhite, this.getType());
  }
}
