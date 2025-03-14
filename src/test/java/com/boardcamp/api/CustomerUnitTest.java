package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCpfConflictException;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomerService;

@SpringBootTest
class CustomerUnitTest {

  @InjectMocks
  private CustomerService customerService;

  @Mock
  private CustomerRepository customerRepository;

  @Test
  void givenRepeatedCustomer_whenCreateCustomer_thenThrowCustomerCpfConflictException() {
		// Arrange
		CustomerDTO body = new CustomerDTO("11111111111", "11111111111", "test");
		doReturn(true).when(customerRepository).existsByCpf(any());
		// Act
		CustomerCpfConflictException exception = assertThrows(CustomerCpfConflictException.class, () -> {
			customerService.createCustomer(body);
		});
		// Assert
		verify(customerRepository, times(1)).existsByCpf(any());
		verify(customerRepository, times(0)).save(any());
		assertNotNull(exception);
		assertEquals("Customer with this CPF already exists.", exception.getMessage());
	}

  // Test create customer - valid customer
  @Test
  void givenValidCustomer_whenCreateCustomer_thenCreateCustomer() {
    // Arrange
    CustomerDTO body = new CustomerDTO("11111111111", "11111111111", "test");
    doReturn(false).when(customerRepository).existsByCpf(any());
    CustomerModel expectedCustomer = new CustomerModel(body);
    doReturn(expectedCustomer).when(customerRepository).save(any());
    // Act
    CustomerModel result = customerService.createCustomer(body);
    // Assert
    verify(customerRepository, times(1)).existsByCpf(any());
    verify(customerRepository, times(1)).save(any());
    assertEquals(expectedCustomer, result);
  }
  
  // Test get all customers
  @Test
  void givenCustomers_whenGetAllCustomers_thenReturnCustomers() {
    // Arrange
    CustomerModel customer1 = new CustomerModel(new CustomerDTO("11111111111", "11111111111", "customer1"));
    CustomerModel customer2 = new CustomerModel(new CustomerDTO("22222222222", "22222222222", "customer2"));
    List<CustomerModel> expectedCustomers = List.of(customer1, customer2);
    doReturn(expectedCustomers).when(customerRepository).findAll();
    // Act
    List<CustomerModel> result = customerService.getAllCustomers();
    // Assert
    verify(customerRepository, times(1)).findAll();
    assertEquals(2, result.size());
    assertEquals(expectedCustomers, result);
  }

  // Test get customer by id
  @Test
  void givenCustomer_whenGetCustomerById_thenReturnCustomer() {
    // Arrange
    CustomerModel customer = new CustomerModel(new CustomerDTO("11111111111", "11111111111", "teste"));
    doReturn(Optional.of(customer)).when(customerRepository).findById(any());
    // Act`
    CustomerModel result = customerService.getCustomerById(customer.getId());
    // Assert
    verify(customerRepository, times(1)).findById(any());
    assertEquals(customer, result);
  }

  // Test get customer by id - customer not found
  @Test
  void givenCustomerNotFound_whenGetCustomerById_thenThrowCustomerNotFoundException() {
    // Arrange
    doReturn(Optional.empty()).when(customerRepository).findById(any());
    // Act
    CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
      customerService.getCustomerById(1L);
    });
    // Assert
    verify(customerRepository, times(1)).findById(any());
    assertNotNull(exception);
    assertEquals("Customer not found.", exception.getMessage());
  }

}
