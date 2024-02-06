package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
@Jacksonized
public class LoginDto {
    @NotBlank
    String username;
    @NotBlank
    @Size(min = 8, max = 500)
    String password;
}
