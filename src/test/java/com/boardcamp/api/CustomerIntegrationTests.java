package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    CustomerModel createdCustomer = customerRepository.save(new CustomerModel(null, "11111111111", "11111111111", "test"));
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

}
