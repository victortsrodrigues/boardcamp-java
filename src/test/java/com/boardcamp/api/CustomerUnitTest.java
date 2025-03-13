package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCpfConflictException;
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
  
}
