package com.boardcamp.api.models;

import com.boardcamp.api.dtos.GamesDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "games")
public class GamesModel {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = true)
  private String image;

  @Column(nullable = false)
  private int stockTotal;

  @Column(nullable = false)
  private int pricePerDay;

  public GamesModel(GamesDTO body) {
    this.name = body.getName();
    this.image = body.getImage();
    this.stockTotal = body.getStockTotal();
    this.pricePerDay = body.getPricePerDay();
  }

}
