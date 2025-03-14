package com.boardcamp.api.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rentals")
public class RentalsModel {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customerId")
  private CustomerModel customer;

  @ManyToOne
  @JoinColumn(name = "gameId")
  private GamesModel game;

  @Column(nullable = false)
  private LocalDate rentDate;

  @Column(nullable = false)
  private int daysRented;
  
  @Column
  private LocalDate returnDate;

  @Column(nullable = false)
  private int originalPrice;

  @Column(nullable = false)
  private int delayFee;


  public RentalsModel(LocalDate rentDate, int daysRented, LocalDate returnDate, int originalPrice, int delayFee, CustomerModel customer, GamesModel game) {
    this.rentDate = rentDate;
    this.daysRented = daysRented;
    this.returnDate = returnDate;
    this.originalPrice = originalPrice;
    this.delayFee = delayFee;
    this.customer = customer;
    this.game = game;
  }

}