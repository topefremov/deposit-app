package ru.alexefremov.depositapp.depositservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexefremov.depositapp.depositservice.dto.LoginDto;
import ru.alexefremov.depositapp.depositservice.dto.TokenDto;
import ru.alexefremov.depositapp.depositservice.security.AuthService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenDto> auth(@RequestBody LoginDto loginDto) {
        String token = authService.authenticate(loginDto.getUsername(), loginDto.getPassword());

        TokenDto tokenDto = TokenDto.builder()
                .userId(SecurityContextHolder.getContext().getAuthentication().getName())
                .token(token)
                .build();
        return ResponseEntity.ok(tokenDto);
    }
}
