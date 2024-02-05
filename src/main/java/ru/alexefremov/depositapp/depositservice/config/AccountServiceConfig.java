package ru.alexefremov.depositapp.depositservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alexefremov.depositapp.depositservice.config.props.ApplicationProperties;
import ru.alexefremov.depositapp.depositservice.repository.AccountEntityRepository;
import ru.alexefremov.depositapp.depositservice.service.AccountService;

@Configuration
@RequiredArgsConstructor
public class AccountServiceConfig {
    private final ApplicationProperties props;

    @Bean
    public AccountService accountService(AccountEntityRepository accountsRepository) {
        return new AccountService(accountsRepository, props.getDepositLimitPercent());
    }
}
