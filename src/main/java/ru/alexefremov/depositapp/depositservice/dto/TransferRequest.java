package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Value
@Builder
@Jacksonized
public class TransferRequest {
    @NotNull
    @Positive
    BigDecimal value;
}
