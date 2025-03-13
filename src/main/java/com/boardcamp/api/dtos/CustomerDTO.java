package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDTO {
  
  @NotBlank(message = "CPF cannot be null or empty")
    @Size(min = 11, max = 11, message = "CPF must have exactly 11 characters")
    private String cpf;

    @Pattern(regexp = "\\d{10,11}", message = "Phone must have 10 or 11 numeric characters")
    private String phone;

    @NotBlank(message = "Name cannot be null or empty")
    private String name;

}
