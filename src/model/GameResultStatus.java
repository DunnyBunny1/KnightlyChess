package model;

/**
 * Represents a status for games of chess that are over.
 */
public enum GameResultStatus {
  CHECKMATE,
  STALEMATE,
  DRAW_BY_INSUFFICIENT_MATERIAL,
  WIN_BY_RESIGNATION,
  DRAW_BY_AGREEMENT
}
