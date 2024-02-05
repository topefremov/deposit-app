package ru.alexefremov.depositapp.depositservice.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alexefremov.depositapp.depositservice.repository.AccountEntityRepository;
import ru.alexefremov.depositapp.depositservice.service.AccountService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class CalculationOfInterestJob {

    private final AccountEntityRepository accountsRepository;
    private final AccountService accountService;
    @Scheduled(fixedRate = 30, initialDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void calculateInterest() {
        List<Long> accountsIds = accountsRepository.findAllAccountsIds();
        for (Long accountId : accountsIds) {
            accountService.calculateInterest(accountId);
        }
    }
}
