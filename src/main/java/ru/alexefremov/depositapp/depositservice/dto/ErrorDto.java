package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@Jacksonized
public class ErrorDto {
    int code;
    String description;
    @Builder.Default
    List<String> details = new ArrayList<>();
}
