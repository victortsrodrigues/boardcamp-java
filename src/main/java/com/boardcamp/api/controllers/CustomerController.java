package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.services.CustomerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/customers")
public class CustomerController {
  
  final CustomerService customerService;

  CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping()
  public ResponseEntity<CustomerModel> createCustomer(@RequestBody @Valid CustomerDTO body) {      
      return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(body));
  }
  
  @GetMapping()
  public ResponseEntity<Object> getCostumers() {
      return ResponseEntity.status(HttpStatus.OK).body(customerService.getAllCustomers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerModel> getCustomerById(@PathVariable("id") Long id) {
      return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerById(id));
  }

}
