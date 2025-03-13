package com.boardcamp.api.exceptions;

public class GamesNameConflictException extends RuntimeException {
  public GamesNameConflictException(String message) {
    super(message);
  }
}
