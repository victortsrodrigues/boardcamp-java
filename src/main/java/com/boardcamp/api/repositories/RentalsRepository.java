package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardcamp.api.models.RentalsModel;

public interface RentalsRepository extends JpaRepository<RentalsModel, Long> {
  
}
