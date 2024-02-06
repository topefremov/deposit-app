package ru.alexefremov.depositapp.depositservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class SearchRequestDto {
    String name;
    @Email
    String email;
    @Pattern(regexp = "^[1-9]{1,13}$")
    String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Parameter(example = "01.01.1900", schema = @Schema(type = "date"))
    LocalDate dateOfBirth;
}
