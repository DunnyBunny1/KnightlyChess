package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
public class StrictChessModelTests {
  private static final long[] numPositionsAtDepth = new long[]{
          1, 20, 400, 8902, 197281, 4865609, 119060324, 3195901860L, 84998978956L, 2439530234167L
  };
  @Test
  public void modelCorrectlyInitializesBoardForStartingPosition() {
    try {
      StrictChessModel model = new StrictChessModel.Builder(StrictChessModel.STARTING_POSITION).build();
    } catch (Exception e) {
      Assert.fail("Model should not throw exception for valid fen string ");
    }
  }

  @Test
  public void MoveGenerationTest(){
    MutableChessModel model = new StrictChessModel.Builder(StrictChessModel.STARTING_POSITION).build();
    model.startGame();
    for(int i = 0; i < numPositionsAtDepth.length; i++){
      Assert.assertEquals(numPositionsAtDepth[i], countLegalMoves(model, i));
    }
  }

  private int countLegalMoves(MutableChessModel model, int depth) {
    if (depth == 0) {
      return 1;
    }
    Set<Move> moves = new HashSet<>();
    moves.addAll(model.getLegalMoves(PlayerColor.BLACK));
    moves.addAll(model.getLegalMoves(PlayerColor.WHITE));
    int numPositions = 0;
    for (Move move : moves) {
      MutableChessModel copy = model.getStrictDeepCopy();
      numPositions += countLegalMoves(copy, depth - 1);
    }
    return numPositions;
  }
}
