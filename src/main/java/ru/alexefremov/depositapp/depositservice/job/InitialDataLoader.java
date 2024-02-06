package ru.alexefremov.depositapp.depositservice.job;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.event.UserChangedEvent;
import ru.alexefremov.depositapp.depositservice.repository.UserEntityRepository;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Используется только лишь для того, чтобы выполнить первоначальную индексацию Эластика.
 * В реальности конечно же эластик будет проиндексирован заранее
 */
@Component
@ConditionalOnProperty(prefix = "app", value = "initial-load", havingValue = "true")
@ParametersAreNonnullByDefault
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationListener<ApplicationReadyEvent> {
    private final UserEntityRepository usersRepository;
    private final ApplicationEventPublisher publisher;
    @Override
    @Transactional(readOnly = true)
    public void onApplicationEvent(ApplicationReadyEvent ignored) {
        usersRepository.findAllUsersIds().forEach(userId -> publisher.publishEvent(new UserChangedEvent(userId)));
    }
}
