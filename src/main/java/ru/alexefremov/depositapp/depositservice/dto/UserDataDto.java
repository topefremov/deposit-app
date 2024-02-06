package ru.alexefremov.depositapp.depositservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    LocalDate dateOfBirth;
    List<PhoneDto> phones;
    List<EmailDto> emails;
    AccountDto account;
}
