package ru.alexefremov.depositapp.depositservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.event.UserChangedEvent;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final PhoneService phoneService;
    private final EmailService emailService;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone addPhone(long userId, String number) {
        Phone phone = phoneService.addPhone(userId, number);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone changePhone(long userId, long phoneId, String newNumber) {
        Phone phone = phoneService.changePhone(userId, phoneId, newNumber);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Phone deletePhone(long userId, long phoneId) {
        Phone phone = phoneService.deletePhone(userId, phoneId);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return phone;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email addEmail(long userId, String emailValue) {
        Email email = emailService.addEmail(userId, emailValue);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return email;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email changeEmail(long userId, long emailId, String newEmail) {
        Email email = emailService.changeEmail(userId, emailId, newEmail);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return email;
    }

    @Transactional
    @PreAuthorize("@authorizationChecker.isOwnerOfResource(#userId)")
    public Email deleteEmail(long userId, long emailId) {
        Email email = emailService.deleteEmail(userId, emailId);
        eventPublisher.publishEvent(new UserChangedEvent(userId));
        return email;
    }

    public Account transferMoney(long toUserId, BigDecimal amount) {
        long authenticatedUserId;
        try {
            authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("User id is not a numeric value");
        }
        return accountService.transferMoney(authenticatedUserId, toUserId, amount);
    }
}
