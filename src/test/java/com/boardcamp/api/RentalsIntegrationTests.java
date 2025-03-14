package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.RentalsDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.models.RentalsModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.repositories.RentalsRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RentalsIntegrationTests {
  
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private RentalsRepository rentalsRepository;

  @Autowired
  private GamesRepository gamesRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @BeforeEach
  void cleanUpDB() {
    rentalsRepository.deleteAll();
    gamesRepository.deleteAll();
    customerRepository.deleteAll();
  }

  // Test create rental - game not found
  @Test
  void givenGameNotFound_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    GamesModel game = new GamesModel(null, "Test", "Test", 10, 10);
    GamesModel deletedGame = gamesRepository.save(game);
    gamesRepository.deleteById(deletedGame.getId());
    RentalsDTO rental = new RentalsDTO(1L, 1L, 1);
    HttpEntity<RentalsDTO> body = new HttpEntity<>(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
      "/rentals", 
      HttpMethod.POST, 
      body, 
      String.class);
    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
    assertEquals("Game not found.", response.getBody());
  }

  @Test
  void givenGameNotAvailable_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    GamesModel game = new GamesModel(null, "Test", "Test", 0, 10);
    GamesModel savedGame = gamesRepository.save(game);
    RentalsDTO rental = new RentalsDTO(1L, savedGame.getId(), 1);
    HttpEntity<RentalsDTO> body = new HttpEntity<>(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
      "/rentals",
      HttpMethod.POST,
      body,
      String.class);
    // Assert
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
    assertEquals("Game not available.", response.getBody());
  }

  // Test create rental - customer not found
  @Test
  void givenCustomerNotFound_whenCreateRental_thenThrowGameNotFoundException() {
    // Arrange
    GamesModel game = new GamesModel(null, "Test", "Test", 10, 10);
    GamesModel savedGame = gamesRepository.save(game);
    CustomerModel customer = new CustomerModel(null, "11111111111", "11111111111", "Test");
    CustomerModel deletedCustomer = customerRepository.save(customer);
    customerRepository.deleteById(deletedCustomer.getId());
    RentalsDTO rental = new RentalsDTO(deletedCustomer.getId(), savedGame.getId(), 1);
    HttpEntity<RentalsDTO> body = new HttpEntity<>(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
      "/rentals",
      HttpMethod.POST,
      body,
      String.class);
    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
    assertEquals("Customer not found.", response.getBody());
  }

  // Test create rental - success
  @Test
  void givenValidData_whenCreateRental_thenSuccess() {
    // Arrange
    GamesModel game = new GamesModel(null, "Test", "Test", 10, 10);
    GamesModel savedGame = gamesRepository.save(game);
    CustomerModel customer = new CustomerModel(null, "11111111111", "11111111111", "Test");
    CustomerModel savedCustomer = customerRepository.save(customer);
    RentalsDTO rental = new RentalsDTO(savedCustomer.getId(), savedGame.getId(), 1);
    HttpEntity<RentalsDTO> body = new HttpEntity<>(rental);
    // Act
    ResponseEntity<RentalsModel> response = restTemplate.exchange(
      "/rentals",
      HttpMethod.POST,
      body,
      RentalsModel.class);
    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, rentalsRepository.count());
    assertEquals(1, rentalsRepository.findAll().size());
  }
}
