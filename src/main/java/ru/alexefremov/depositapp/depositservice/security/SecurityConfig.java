package ru.alexefremov.depositapp.depositservice.security;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.alexefremov.depositapp.depositservice.config.props.ApplicationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final ApplicationProperties props;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                .antMatchers("/auth").permitAll()
                                .mvcMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .antMatchers("/**").authenticated());
//                .anyRequest().authenticated();

        http.addFilterBefore(new JwtAuthFilter(jwtUtils()), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public SecretKey jwsSecretKey() {
        return Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwsSecretKey(), props.getJwt().getExpirationTime());
    }
}
