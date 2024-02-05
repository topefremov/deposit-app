package ru.alexefremov.depositapp.depositservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String authenticate(String username, String password) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication= authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateToken(authentication.getName(), authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    }
}
