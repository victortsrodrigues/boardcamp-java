package com.boardcamp.api.exceptions;

public class RentalsNotFoundException extends RuntimeException {
  public RentalsNotFoundException(String message) {
    super(message);
  }
  
}
