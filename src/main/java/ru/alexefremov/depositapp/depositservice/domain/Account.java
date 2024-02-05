package ru.alexefremov.depositapp.depositservice.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder
@Jacksonized
public class Account {
    long id;
    BigDecimal initialBalance;
    BigDecimal balance;
}
