package com.boardcamp.api.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

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

@Service
public class RentalsService {

  final RentalsRepository rentalsRepository;
  final GamesRepository gamesRepository;
  final CustomerRepository customerRepository;

  public RentalsService(RentalsRepository rentalsRepository, GamesRepository gamesRepository,
      CustomerRepository customerRepository) {
    this.rentalsRepository = rentalsRepository;
    this.gamesRepository = gamesRepository;
    this.customerRepository = customerRepository;
  }

  // Service method to create a new rental
  public RentalsModel createRental(RentalsDTO body) {
    // Error handling
    GamesModel game = gamesRepository.findById(body.getGameId())
        .orElseThrow(() -> new GameNotFoundException("Game not found."));
    if (game.getStockTotal() == 0) {
      throw new StockUnprocessableException("Game not available.");
    }
    CustomerModel customer = customerRepository.findById(body.getCustomerId())
        .orElseThrow(() -> new CustomerNotFoundException("Customer not found."));
    // Defining the rental values
    LocalDate rentDate = LocalDate.now();
    int originalPrice = body.getDaysRented() * game.getPricePerDay();
    LocalDate returnDate = null;
    int delayFee = 0;

    return rentalsRepository
        .save(new RentalsModel(rentDate, body.getDaysRented(), returnDate, originalPrice, delayFee, customer, game));
  }

}
