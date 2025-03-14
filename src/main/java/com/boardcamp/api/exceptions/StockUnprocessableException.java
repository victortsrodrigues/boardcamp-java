package com.boardcamp.api.exceptions;

public class StockUnprocessableException extends RuntimeException {
  public StockUnprocessableException(String message) {
    super(message);
  }
}
