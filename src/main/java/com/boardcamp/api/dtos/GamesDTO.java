package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GamesDTO {
  
  @NotBlank(message = "Name cannot be null or empty")
  private String name;

  private String image;

  @NotNull
  @Positive(message = "Stock total must be a positive number")
  private int stockTotal;

  @NotNull
  @Positive(message = "Price per day must be a positive number")
  private int pricePerDay;

}
