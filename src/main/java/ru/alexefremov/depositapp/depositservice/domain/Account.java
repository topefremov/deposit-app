package ru.alexefremov.depositapp.depositservice.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Account {
    long id;
    BigDecimal initialBalance;
    BigDecimal balance;
}
