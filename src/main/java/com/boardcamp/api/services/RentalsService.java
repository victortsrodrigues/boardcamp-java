package com.boardcamp.api.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalsDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.RentalsNotFoundException;
import com.boardcamp.api.exceptions.RentalsUnprocessableException;
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
    LocalDate rentDate = LocalDate.of(2025, 3, 11);
    int originalPrice = body.getDaysRented() * game.getPricePerDay();
    LocalDate returnDate = null;
    int delayFee = 0;

    return rentalsRepository
        .save(new RentalsModel(rentDate, body.getDaysRented(), returnDate, originalPrice, delayFee, customer, game));
  }

  // Service method to get all rentals
  public List<RentalsModel> getAllRentals() {
    return rentalsRepository.findAll();
  }

  // Service method to UPDATE a rental by ID
  public RentalsModel updateRental(Long id) {
    // Error handling
    RentalsModel rental = rentalsRepository.findById(id)
        .orElseThrow(() -> new RentalsNotFoundException("Rental not found."));
    if (rental.getReturnDate() != null) {
      throw new RentalsUnprocessableException("Rental already returned.");
    }
    rental.setReturnDate(LocalDate.now());
    long delayDays = ChronoUnit.DAYS.between(rental.getRentDate(), LocalDate.now());
    if (delayDays < 0) {
      delayDays = 0;
    }
    rental.setDelayFee((int) delayDays * rental.getGame().getPricePerDay());
    return rentalsRepository.save(rental);
  }

}
