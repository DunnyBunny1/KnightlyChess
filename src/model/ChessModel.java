package model;

import java.util.Optional;

public class ChessModel implements MutableChessModel{
  public static final int NUM_RANKS = 8;
  public static final int NUM_FILES = 8;


  private ChessModel(Builder builder){

  }

  /**
   * Private constructor used for deepCopy()
   */
  private ChessModel(){

  }

  public static class Builder{
    public Builder(String fen){

    }

    /**
     * Package-private constructor for testing pruposes
     */
    Builder(){

    }


  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public void makeMove(Move m) throws IllegalStateException {

  }

  @Override
  public void startGame() {

  }

  @Override
  public boolean canMakeMove(Move m) {
    return false;
  }

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public GameResultStatus getFinalGameStatus() {
    return null;
  }

  @Override
  public PlayerColor getTurn() {
    return null;
  }

  @Override
  public ChessModel getDeepCopy() {
    return null;
  }

  @Override
  public void addListener(ModelListener listener) {

  }

  @Override
  public Optional<Piece> getPieceAt(RowColPair pair) {
    return Optional.empty();
  }
}
