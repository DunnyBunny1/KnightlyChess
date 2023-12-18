package model;

/**
 * Represents a status for games of chess that are over.
 */
public enum GameResultStatus {
  CHECKMATE_BY_BLACK,
  CHECKMATE_BY_WHITE,
  STALEMATE,
  DRAW_BY_INSUFFICIENT_MATERIAL;
//  WIN_BY_RESIGNATION,
//  DRAW_BY_AGREEMENT
}
