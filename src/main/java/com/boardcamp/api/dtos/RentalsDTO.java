package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalsDTO {
  
  @NotNull
  private Long customerId;

  @NotNull
  private Long gameId;

  @NotNull
  @Positive
  private int daysRented;

}
