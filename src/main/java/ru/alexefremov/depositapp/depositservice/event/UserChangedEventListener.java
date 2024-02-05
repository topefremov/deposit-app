package ru.alexefremov.depositapp.depositservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserChangedEventListener {


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processEvent(UserChangedEvent event) {
        log.info("USER CHANGED");
    }
}
