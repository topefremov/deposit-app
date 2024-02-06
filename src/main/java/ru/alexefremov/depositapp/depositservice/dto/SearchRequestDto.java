package ru.alexefremov.depositapp.depositservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class SearchRequestDto {
    String name;
    String email;
    String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Parameter(example = "01.01.1990", schema = @Schema(type = "date"))
    LocalDate dateOfBirth;
}
