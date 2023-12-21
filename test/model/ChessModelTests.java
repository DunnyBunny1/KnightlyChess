package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
public class ChessModelTests {
  @Test
  public void modelCorrectlyInitializesBoardForStartingPosition() {
    try {
      ChessModel model = new ChessModel.Builder(ChessModel.STARTING_POSITION).build();
    } catch (Exception e) {
      Assert.fail("Model should not throw exception for valid fen string ");
    }
  }

  /**
   *
   * @param model
   * @param depth the depth in ply
   * @return
   */
  public int countLegalMoves(MutableChessModel model, int depth) {
    if (depth == 0) {
      return 1;
    }
    Set<Move> moves = new HashSet<>();
    moves.addAll(model.getLegalMoves(PlayerColor.BLACK));
    moves.addAll(model.getLegalMoves(PlayerColor.WHITE));
    int numPositions = 0;
    for (Move move : moves) {
      MutableChessModel copy = model.getMutableDeepCopy();
      numPositions += countLegalMoves(copy, depth - 1);
    }
    return numPositions;
  }

  @Test
  public void MoveGenerationTest(){

  }
}
