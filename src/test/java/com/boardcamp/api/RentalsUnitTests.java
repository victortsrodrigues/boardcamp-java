package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.RentalsDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.StockUnprocessableException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.models.RentalsModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.repositories.RentalsRepository;
import com.boardcamp.api.services.RentalsService;

@SpringBootTest
public class RentalsUnitTests {
  
  @InjectMocks
  private RentalsService rentalsService;

  @Mock
  private RentalsRepository rentalsRepository;

  @Mock
  private GamesRepository gamesRepository;

  @Mock
  private CustomerRepository customerRepository;

  // Test create rental - game not found
  @Test
  void givenGameNotFound_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    RentalsDTO rental = new RentalsDTO(1L, 1L, 1);
    doReturn(Optional.empty()).when(gamesRepository).findById(any());
    // Act
    GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> {
      rentalsService.createRental(rental);
    });
    // Assert
    verify(gamesRepository, times(1)).findById(any());
    verify(rentalsRepository, times(0)).save(any());
    assertNotNull(exception);
    assertEquals("Game not found.", exception.getMessage());
  }

  // Test create rental - game not available
  @Test
  void givenGameNotAvailable_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    RentalsDTO rental = new RentalsDTO(1L, 1L, 1);
    GamesModel game = new GamesModel();
    game.setStockTotal(0);
    doReturn(Optional.of(game)).when(gamesRepository).findById(any());
    // Act
    StockUnprocessableException exception = assertThrows(StockUnprocessableException.class, () -> {
      rentalsService.createRental(rental);
    });
    // Assert
    assertNotNull(exception);
    assertEquals("Game not available.", exception.getMessage());
    verify(customerRepository, times(0)).findById(any());
  }

  // Test create rental - customer not found
  @Test
  void givenCustomerNotFound_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    RentalsDTO rental = new RentalsDTO(1L, 1L, 1);
    GamesModel game = new GamesModel();
    game.setStockTotal(1);
    doReturn(Optional.of(game)).when(gamesRepository).findById(any());
    doReturn(Optional.empty()).when(customerRepository).findById(any());
    // Act
    CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
      rentalsService.createRental(rental);
    });
    // Assert
    verify(gamesRepository, times(1)).findById(any());
    verify(rentalsRepository, times(0)).save(any());
    assertNotNull(exception);
    assertEquals("Customer not found.", exception.getMessage());
  }

  // Test create rental - success
  @Test
  void givenValidRental_whenCreateRental_thenSuccess() {
    // Arrange
    RentalsDTO rental = new RentalsDTO(1L, 1L, 1);
    GamesModel game = new GamesModel(1L, "test", "test", 10, 10);
    doReturn(Optional.of(game)).when(gamesRepository).findById(any());
    CustomerModel customer = new CustomerModel(1L, "11111111111", "11111111111", "teste");
    doReturn(Optional.of(customer)).when(customerRepository).findById(any());
    RentalsModel newRental = new RentalsModel(
      LocalDate.now(),
      1,
      null,
      game.getPricePerDay(),
      0,
      customer,
      game);
    doReturn(newRental).when(rentalsRepository).save(any());
    // Act
    RentalsModel result = rentalsService.createRental(rental);
    // Assert
    assertNotNull(result);
    assertEquals(rental.getGameId(), result.getGame().getId());
    assertEquals(rental.getCustomerId(), result.getCustomer().getId());
  }
}
