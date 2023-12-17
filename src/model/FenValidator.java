package model;

public final class FenValidator {
  // Method to check if a FEN string is a legal FEN string
  public static boolean isValidFEN(String fenString) {
    if (fenString == null || fenString.isEmpty()) {
      return false;
    }
    // Splitting the FEN string into its components
    String[] fenParts = fenString.split(" ");

    return fenParts.length == 6 &&
            isValidBoardConfiguration(fenParts[0]) &&
            isValidActiveColor(fenParts[1]) &&
            isValidCastlingAvailability(fenParts[2]) &&
            isValidEnPassantTarget(fenParts[3]) &&
            isValidHalfMoveClock(fenParts[4]) &&
            isValidFullMoveNumber(fenParts[5]);
  }

  // Method to validate the board configuration
  private static boolean isValidBoardConfiguration(String boardConfig) {
    // Check if the board configuration has the correct number of rows and pieces
    String[] ranks = boardConfig.split("/");
    if (ranks.length != ChessModel.NUM_RANKS) {
      return false;
    }
    for (String row : ranks) {
      if (!isValidRank(row)) {
        return false;
      }
    }

    int whiteKingCount = 0, blackKingCount = 0;
    for (char c : boardConfig.toCharArray()) {
      if (c == 'K') {
        whiteKingCount++;
      } else if (c == 'k') {
        blackKingCount++;
      }
    }
    return whiteKingCount == 1 && blackKingCount == 1;
  }

  // Method to validate a single row of the board configuration
  private static boolean isValidRank(String rank) {
    int count = 0;
    for (char c : rank.toCharArray()) {
      // Check if it's a valid piece or a digit representing empty squares
      if (Character.isDigit(c)) {
        count += Character.getNumericValue(c);
      } else if ("pnbrqkPNBRQK".indexOf(c) == -1) {
        // Invalid piece encountered
        return false;
      } else {
        count++;
      }
    }

    // Check if the row has exactly 8 squares
    return count == ChessModel.NUM_FILES;
  }

  // Method to validate the active color
  private static boolean isValidActiveColor(String activeColor) {
    // Check if it's 'w' (for white) or 'b' (for black)
    return activeColor.equals("w") || activeColor.equals("b");
  }

  // Method to validate castling availability
  private static boolean isValidCastlingAvailability(String castlingAvailability) {
    // Check if castling availability string contains valid characters: 'KQkq' or '-'
    return castlingAvailability.matches("[KQkq-]+");
  }

  // Method to validate en passant target square
  private static boolean isValidEnPassantTarget(String enPassantTarget) {
    // Check if it's a valid square or '-'
    return enPassantTarget.equals("-") || (enPassantTarget.length() == 2
            && enPassantTarget.charAt(0) >= 'a' && enPassantTarget.charAt(0) <= 'h'
            && enPassantTarget.charAt(1) >= '1' && enPassantTarget.charAt(1) <= '8');
  }

  // Method to validate halfmove clock
  private static boolean isValidHalfMoveClock(String halfMoveClock) {
    // Check if it's a number within valid range (0 to 100)
    try {
      int halfMoves = Integer.parseInt(halfMoveClock);
      return halfMoves >= 0 && halfMoves <= 100;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  // Method to validate fullmove number
  private static boolean isValidFullMoveNumber(String fullMoveNumber) {
    // Check if it's a positive number
    try {
      int fullMoves = Integer.parseInt(fullMoveNumber);
      return fullMoves > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  // Method to ensure each color has one king
  private static boolean hasOneKingEachColor(String boardConfig) {
    int whiteKingCount = countOccurrences(boardConfig, 'K');
    int blackKingCount = countOccurrences(boardConfig, 'k');
    return whiteKingCount == 1 && blackKingCount == 1;
  }

  // Method to count occurrences of a specific character in the board configuration
  private static int countOccurrences(String boardConfig, char piece) {
    int count = 0;
    for (char c : boardConfig.toCharArray()) {
      if (c == piece) {
        count++;
      }
    }
    return count;
  }

  // Method to ensure the relationship between half move and full move counters is valid
  private static boolean isMoveCounterRelationshipValid(String halfMoveCount, String fullMoveCount) {
    try {
      int halfMoves = Integer.parseInt(halfMoveCount);
      int fullMoves = Integer.parseInt(fullMoveCount);

      // Ensure the half move counter is not greater than twice the full move counter
      return !(halfMoves > fullMoves * 2 || halfMoves < fullMoves);
    } catch (NumberFormatException e) {
      return false;
    }
  }

}
