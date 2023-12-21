package model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PermissibleChessModelImpl implements PermissiveChessModel {
  private final StrictChessModel delegate;
  private final Set<ModelListener> listeners;

  public PermissibleChessModelImpl(StrictChessModel delegate) {
   this.delegate = delegate;
   this.listeners = new HashSet<>();
  }

  @Override
  public boolean canMakeMove(Move m) {

  }

  @Override
  public void makePseudoLegalMove(Move m) {

  }

  @Override
  public Optional<PieceType> getPieceTypeAt(RowColPair pair) {
    return delegate.getPieceTypeAt(pair);
  }



  @Override
  public boolean isGameOver() {
    return delegate.isGameOver();
  }

  @Override
  public GameResultStatus getFinalGameStatus() {
    return delegate.getFinalGameStatus();
  }

  @Override
  public boolean getWhiteToMove() {
    return delegate.getWhiteToMove();
  }

  @Override
  public MutableChessModel getStrictDeepCopy() {
    return delegate.getStrictDeepCopy();
  }

  @Override
  public PermissiveChessModel getPermissibleDeepCopy() {
    return new PermissibleChessModelImpl(delegate.getStrictDeepCopy());
  }

  @Override
  public void addListener(ModelListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public Set<Move> getLegalMoves(PlayerColor c) {
    return delegate.getLegalMoves(c);
  }

  @Override
  public Set<RowColPair> getColorTargetSquares(PlayerColor c) {
    return delegate.getColorTargetSquares(c);
  }

  @Override
  public Optional<Set<RowColPair>> getTargetSquares(RowColPair position) {
    return delegate.getTargetSquares(position);
  }

  @Override
  public RowColPair getKingSquare(PlayerColor c) {
    return delegate.getKingSquare(c);
  }

  @Override
  public String getLetterSquareCombination(RowColPair position) {
    return delegate.getLetterSquareCombination(position);
  }

  @Override
  public Optional<Piece>[][] getBoardCopy() {
    return delegate.getBoardCopy();
  }

  @Override
  public boolean isInBounds(RowColPair pair) {
    return delegate.isInBounds(pair);
  }

  @Override
  public String getCastlingPrivileges() {
    return delegate.getCastlingPrivileges();
  }

  @Override
  public Optional<RowColPair> getEnPassantTarget() {
    return delegate.getEnPassantTarget();
  }
}
