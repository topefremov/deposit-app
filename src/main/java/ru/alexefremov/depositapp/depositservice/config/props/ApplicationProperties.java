package ru.alexefremov.depositapp.depositservice.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app")
@Configuration
@Getter
@Setter
public class ApplicationProperties {

    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private Long expirationTime;
    }
}
