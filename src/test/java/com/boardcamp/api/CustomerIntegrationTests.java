package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerIntegrationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private CustomerRepository customerRepository;

  @BeforeEach
  void cleanUp() {
    customerRepository.deleteAll();
  }

  @Test
  void givenRepeatedCustomer_whenCreateCustomer_thenThrowCustomerCpfConflictException() {
    // Arrange
    CustomerModel createdCustomer = customerRepository
        .save(new CustomerModel(null, "11111111111", "11111111111", "test"));
    CustomerDTO customerDTO = new CustomerDTO(createdCustomer.getCpf(), createdCustomer.getPhone(),
        createdCustomer.getName());
    HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);
    // Act
    ResponseEntity<String> response = restTemplate.exchange("/customers", HttpMethod.POST, body, String.class);
    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("A customer with this CPF already exists.", response.getBody());
    assertEquals(1, customerRepository.count());
  }

  @Test
  void givenValidCustomer_whenCreateCustomer_thenCreateCustomer() {
    // Arrange
    CustomerDTO customerDTO = new CustomerDTO("11111111111", "11111111111", "test");
    HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);
    // Act
    ResponseEntity<CustomerModel> response = restTemplate.exchange("/customers", HttpMethod.POST, body,
        CustomerModel.class);
    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(customerDTO.getCpf(), response.getBody().getCpf());
    assertEquals(1, customerRepository.count());
  }

  // Test get all customers
  @Test
  void givenCustomers_whenGetAllCustomers_thenReturnCustomers() {
    // Arrange
    customerRepository.save(new CustomerModel(new CustomerDTO("11111111111", "11111111111", "customer1")));
    customerRepository.save(new CustomerModel(new CustomerDTO("22222222222", "22222222222", "customer2")));
    // Act
    ResponseEntity<List<CustomerModel>> response = restTemplate.exchange(
        "/customers",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {});
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    List<CustomerModel> customers = response.getBody();
    assertNotNull(customers);
  }

  // Test get customer by id
  @Test
  void givenCustomer_whenGetCustomerById_thenReturnCustomer() {
    // Arrange
    CustomerModel createdCustomer = customerRepository
        .save(new CustomerModel(null, "11111111111", "11111111111", "test"));
    // Act
    ResponseEntity<CustomerModel> response = restTemplate.getForEntity(
      "/customers/" + createdCustomer.getId(),
      CustomerModel.class);
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(createdCustomer.getId(), response.getBody().getId());
  }

  // Test get customer by id - customer not found
  @Test
  void givenCustomerNotFound_whenGetCustomerById_thenThrowCustomerNotFoundException() {
    // Arrange
    CustomerModel customer = new CustomerModel(null, "11111111111", "11111111111", "test");
    CustomerModel deletedUser = customerRepository.save(customer);
    customerRepository.deleteById(deletedUser.getId());
    // Act
    ResponseEntity<String> response = restTemplate.getForEntity(
      "/customers/" + deletedUser.getId(),
      String.class);
    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Customer not found.", response.getBody());
    assertEquals(0, customerRepository.count());
  }


}
