package ru.alexefremov.depositapp.depositservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
    long id;
    BigDecimal initialBalance;
    BigDecimal currentBalance;
}
