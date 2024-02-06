package ru.alexefremov.depositapp.depositservice.search;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Value
@Builder
public class SearchFilter {
    String name;
    LocalDate dateOfBirth;
    String phone;
    String email;
    Pageable pageable;
}
