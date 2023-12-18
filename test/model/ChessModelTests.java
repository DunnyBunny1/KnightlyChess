package model;
import org.junit.Assert;
import org.junit.Test;

public class ChessModelTests {
  @Test
  public void modelCorrectlyInitializesBoardForStartingPosition(){
    ChessModel model = new ChessModel.Builder(ChessModel.STARTING_POSITION).build();
    System.out.println(model.toString());
  }
}
