package ru.alexefremov.depositapp.depositservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.event.UserChangedEvent;
import ru.alexefremov.depositapp.depositservice.search.SearchFilter;
import ru.alexefremov.depositapp.depositservice.search.UserData;
import ru.alexefremov.depositapp.depositservice.search.UserSearchService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final PhoneService phoneService;
    private final EmailService emailService;
    private final AccountService accountService;
    private final UserSearchService userSearchService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone addPhone(long userId, String number) {
        Phone phone = phoneService.addPhone(userId, number);
        publishUserChangedEvent(userId);
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone changePhone(long userId, long phoneId, String newNumber) {
        Phone phone = phoneService.changePhone(userId, phoneId, newNumber);
        publishUserChangedEvent(userId);
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone deletePhone(long userId, long phoneId) {
        Phone phone = phoneService.deletePhone(userId, phoneId);
        publishUserChangedEvent(userId);
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email addEmail(long userId, String emailValue) {
        Email email = emailService.addEmail(userId, emailValue);
        publishUserChangedEvent(userId);
        return email;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email changeEmail(long userId, long emailId, String newEmail) {
        Email email = emailService.changeEmail(userId, emailId, newEmail);
        publishUserChangedEvent(userId);
        return email;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email deleteEmail(long userId, long emailId) {
        Email email = emailService.deleteEmail(userId, emailId);
        publishUserChangedEvent(userId);
        return email;
    }

    @Transactional
    public void calculateInterest(long accountId) {
        if (accountService.calculateInterest(accountId)) {
            publishUserChangedEvent(accountId);
        }
    }

    public Account transferMoney(long toUserId, BigDecimal amount) {
        long authenticatedUserId;
        try {
            authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("User id is not a numeric value");
        }
        Account account = accountService.transferMoney(authenticatedUserId, toUserId, amount);
        publishUserChangedEvent(authenticatedUserId);
        publishUserChangedEvent(toUserId);
        return account;
    }

    public SearchPage<UserData> search(SearchFilter searchFilter) {
        return userSearchService.search(searchFilter);
    }

    private void publishUserChangedEvent(long userId) {
        eventPublisher.publishEvent(new UserChangedEvent(userId));
    }
}
