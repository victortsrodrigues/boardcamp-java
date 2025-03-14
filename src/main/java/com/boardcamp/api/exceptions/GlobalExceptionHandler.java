package com.boardcamp.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({GamesNameConflictException.class})
  public ResponseEntity<String> handleGamesNameConflict(GamesNameConflictException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
  }

  @ExceptionHandler({CustomerCpfConflictException.class})
  public ResponseEntity<String> handleCustomerCpfConflict(CustomerCpfConflictException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
  }

  @ExceptionHandler({CustomerNotFoundException.class})
  public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }

  @ExceptionHandler({GameNotFoundException.class})
  public ResponseEntity<String> handleGameNotFound(GameNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }

  @ExceptionHandler({StockUnprocessableException.class})
  public ResponseEntity<String> handleStockUnprocessable(StockUnprocessableException exception) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
  }

  @ExceptionHandler({RentalsNotFoundException.class})
  public ResponseEntity<String> handleRentalsNotFound(RentalsNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler({RentalsUnprocessableException.class})
  public ResponseEntity<String> handleRentalsUnprocessable(RentalsUnprocessableException exception) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
  }

  @ExceptionHandler({RentalsReturnedException.class})
  public ResponseEntity<String> handleRentalsReturned(RentalsReturnedException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
  }
}
