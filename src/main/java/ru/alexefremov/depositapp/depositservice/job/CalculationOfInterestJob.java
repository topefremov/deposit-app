package ru.alexefremov.depositapp.depositservice.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alexefremov.depositapp.depositservice.service.AccountService;
import ru.alexefremov.depositapp.depositservice.service.UserFacade;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class CalculationOfInterestJob {
    private final AccountService accountService;
    private final UserFacade userFacade;
    @Scheduled(fixedRate = 30, initialDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void calculateInterest() {
        List<Long> accountsIds = accountService.findAllAccountsIds();
        for (Long accountId : accountsIds) {
            userFacade.calculateInterest(accountId);
        }
    }
}
