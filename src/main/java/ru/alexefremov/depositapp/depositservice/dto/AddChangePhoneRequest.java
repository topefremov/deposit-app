package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
@Builder
@Jacksonized
public class AddChangePhoneRequest {
    @NotNull
    @Pattern(regexp = "^[1-9]{1,13}$")
    String number;
}
