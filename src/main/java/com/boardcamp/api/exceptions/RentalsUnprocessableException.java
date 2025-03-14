package com.boardcamp.api.exceptions;

public class RentalsUnprocessableException extends RuntimeException {
  public RentalsUnprocessableException(String message) {
    super(message);
  }
}
