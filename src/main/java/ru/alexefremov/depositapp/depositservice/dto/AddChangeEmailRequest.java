package ru.alexefremov.depositapp.depositservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;

@Value
@Builder
@Jacksonized
public class AddChangeEmailRequest {
    @Email
    String email;
}
