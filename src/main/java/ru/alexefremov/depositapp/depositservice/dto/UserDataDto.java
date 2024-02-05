package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
@Jacksonized
public class UserDataDto {
    long id;
    String name;
    LocalDate dateOfBirth;
    List<PhoneDto> phones;
    List<EmailDto> emails;
    AccountDto account;
}
