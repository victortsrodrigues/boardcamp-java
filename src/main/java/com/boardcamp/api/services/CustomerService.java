package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCpfConflictException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {

  final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  // Method to create a new customer
  public CustomerModel createCustomer(CustomerDTO body) {
    if (customerRepository.existsByCpf(body.getCpf())) {
      throw new CustomerCpfConflictException("A customer with this CPF already exists.");
    }

    return customerRepository.save(new CustomerModel(body));
  }

}
