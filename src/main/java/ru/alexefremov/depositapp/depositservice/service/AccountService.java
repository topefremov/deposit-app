package ru.alexefremov.depositapp.depositservice.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.entity.AccountEntity;
import ru.alexefremov.depositapp.depositservice.exception.BusinessLayerException;
import ru.alexefremov.depositapp.depositservice.repository.AccountEntityRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountEntityRepository accountsRepository;
    private final int depositLimitPercent;

    @Transactional(readOnly = true)
    public List<Long> findAllAccountsIds() {
        return accountsRepository.findAllAccountsIds();
    }
    public boolean calculateInterest(long accountId) {
        AccountEntity account = accountsRepository.findByIdWithLock(accountId);
        if (account == null) {
            throw new BusinessLayerException("Account with id " + accountId + " not found");
        }
        return doCalculateInterest(account);
    }

    public Account transferMoney(long fromUserId, long toUserId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount is less then zero");
        }

        TransferData transferData = lockUsers(fromUserId, toUserId);
        // now we have two accounts locked

        if (fromUserId == toUserId) {
            return Account.builder()
                    .id(transferData.fromAccount.getId())
                    .initialBalance(transferData.fromAccount.getInitialBalance())
                    .balance(transferData.fromAccount.getBalance())
                    .build();
        }

        var fromAccountNewBalance = transferData.fromAccount.getBalance().subtract(amount);
        if (fromAccountNewBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessLayerException("Can't transfer amount of " + amount + ". Insufficient funds");
        }
        var toAccountNewBalance = transferData.toAccount.getBalance().add(amount);
        var maxDeposit = getMaxDepositAvailable(transferData.toAccount.getInitialBalance());
        if (toAccountNewBalance.compareTo(maxDeposit) > 0) {
            throw new BusinessLayerException("Can't transfer amount of " + amount + ". Max deposit has been reached");
        }

        transferData.fromAccount.setBalance(fromAccountNewBalance);
        transferData.toAccount.setBalance(toAccountNewBalance);
        accountsRepository.saveAll(List.of(transferData.fromAccount, transferData.toAccount));
        return Account.builder()
                .id(transferData.fromAccount.getId())
                .initialBalance(transferData.fromAccount.getInitialBalance())
                .balance(transferData.fromAccount.getBalance())
                .build();
    }

    private TransferData lockUsers(long fromUserId, long toUserId) {
        AccountEntity fromAccount;
        AccountEntity toAccount;

        // below statements block account entities in the db
        if (fromUserId <= toUserId) {
            fromAccount = accountsRepository.findByUserIdWithLock(fromUserId);
            toAccount = accountsRepository.findByUserIdWithLock(toUserId);
        } else {
            toAccount = accountsRepository.findByUserIdWithLock(toUserId);
            fromAccount = accountsRepository.findByIdWithLock(fromUserId);
        }

        if (fromAccount == null) {
            throw new BusinessLayerException("Account for user with id " + fromUserId + " not found");
        }

        if (toAccount == null) {
            throw new BusinessLayerException("Account for user with id " + toUserId + " not found");
        }

        return new TransferData(fromAccount, toAccount);
    }

    private boolean doCalculateInterest(AccountEntity account) {
        var currentBalance = account.getBalance();
        var maxDeposit = getMaxDepositAvailable(account.getInitialBalance());
        BigDecimal interest = currentBalance.multiply(new BigDecimal("0.1")).setScale(2, RoundingMode.HALF_UP);
        var newBalance = currentBalance.add(interest);

        boolean success = false;
        if (newBalance.compareTo(maxDeposit) <= 0) {
            account.setBalance(newBalance);
            log.debug("New balance for account {} is {}", account.getId(), account.getBalance());
            success = true;
        } else {
            log.debug("Balance for account {} has reached its maximum. Current balance: {}. Limit: {}", account.getId(), account.getBalance(), maxDeposit);
        }
        return success;
    }

    private BigDecimal getMaxDepositAvailable(BigDecimal balance) {
        BigDecimal multiplier = BigDecimal.valueOf(depositLimitPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return balance.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    @Value
    private static class TransferData {
        AccountEntity fromAccount;
        AccountEntity toAccount;
    }
}
