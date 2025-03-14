package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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

  // Test get all rentals - success
  @Test
  void givenValidRentals_whenGetAllRentals_thenSuccess() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    CustomerModel customer2 = customerRepository.save(new CustomerModel(null, "22222222222", "22222222222", "Test2"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    GamesModel game2 = gamesRepository.save(new GamesModel(null, "Test2", "Test2", 10, 10));
    RentalsModel rental1 = new RentalsModel(
        LocalDate.now(),
        1,
        null,
        10,
        0,
        customer1,
        game1);
    RentalsModel rental2 = new RentalsModel(
        LocalDate.now(),
        1,
        null,
        10,
        0,
        customer2,
        game2);
    rentalsRepository.save(rental1);
    rentalsRepository.save(rental2);
    // Act
    ResponseEntity<List<RentalsModel>> response = restTemplate.exchange(
        "/rentals",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {});
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    List<RentalsModel> rentals = response.getBody();
    assertNotNull(rentals);
  }

  // Test update rental - not found
  @Test
  void givenRentalNotFound_whenUpdateRental_thenThrowRentalNotFoundException() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now(),
        1,
        null,
        10,
        0,
        customer1,
        game1);
    RentalsModel deletedRental = rentalsRepository.save(rental);
    rentalsRepository.deleteById(deletedRental.getId());
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + deletedRental.getId() + "/return",
        HttpMethod.POST,
        null,
        String.class,
        deletedRental.getId());
    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
  }

  // Test update rental - Rental already returned
  @Test
  void givenRentalAlreadyReturned_whenUpdateRental_thenThrowRentalAlreadyReturnedException() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now(),
        1,
        LocalDate.now().plusDays(5),
        10,
        0,
        customer1,
        game1);
    RentalsModel savedRental = rentalsRepository.save(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + savedRental.getId() + "/return",
        HttpMethod.POST,
        null,
        String.class,
        savedRental.getId());
    // Assert
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals(1, rentalsRepository.count());
    assertEquals("Rental already returned.", response.getBody());
  }

  // Test update rental - success
  @Test
  void givenValidRental_whenUpdateRental_thenSuccess() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now().minusDays(5),
        1,
        null,
        10,
        0,
        customer1,
        game1);
    RentalsModel savedRental = rentalsRepository.save(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + savedRental.getId() + "/return",
        HttpMethod.POST,
        null,
        String.class,
        savedRental.getId());
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, rentalsRepository.count());
  }

  // Test delete rental - not found
  @Test
  void givenRentalNotFound_whenDeleteRental_thenThrowRentalNotFoundException() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now(),
        1,
        null,
        10,
        0,
        customer1,
        game1);
    RentalsModel deletedRental = rentalsRepository.save(rental);
    rentalsRepository.deleteById(deletedRental.getId());
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + deletedRental.getId(),
        HttpMethod.DELETE,
        null,
        String.class,
        deletedRental.getId());
    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
  }

  // Test delete rental - rental already returned
  @Test
  void givenRentalAlreadyReturned_whenDeleteRental_thenThrowRentalAlreadyReturnedException() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now(),
        1,
        LocalDate.now().plusDays(5),
        10,
        0,
        customer1,
        game1);
    RentalsModel savedRental = rentalsRepository.save(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + savedRental.getId(),
        HttpMethod.DELETE,
        null,
        String.class,
        savedRental.getId());
    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(1, rentalsRepository.count());
    assertEquals("Rental already returned.", response.getBody());
  }

  // Test delete rental - success
  @Test
  void givenValidRental_whenDeleteRental_thenSuccess() {
    // Arrange
    CustomerModel customer1 = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "Test1"));
    GamesModel game1 = gamesRepository.save(new GamesModel(null, "Test1", "Test1", 10, 10));
    RentalsModel rental = new RentalsModel(
        LocalDate.now().minusDays(5),
        1,
        null,
        10,
        0,
        customer1,
        game1);
    RentalsModel savedRental = rentalsRepository.save(rental);
    // Act
    ResponseEntity<String> response = restTemplate.exchange(
        "/rentals/" + savedRental.getId(),
        HttpMethod.DELETE,
        null,
        String.class,
        savedRental.getId());
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(0, rentalsRepository.count());
  }
}
