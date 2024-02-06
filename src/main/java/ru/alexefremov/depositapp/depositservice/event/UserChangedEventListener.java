package ru.alexefremov.depositapp.depositservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.alexefremov.depositapp.depositservice.repository.UserDataRepository;
import ru.alexefremov.depositapp.depositservice.search.UserData;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserChangedEventListener {
    private final UserDataRepository userDataRepository;
    private final JdbcTemplate jdbcTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(readOnly = true)
    public void processEvent(UserChangedEvent event) {
        String sql = "SELECT u.*, a.balance, e.id AS email_id, e.email, p.id AS phone_id, p.phone FROM \"user\" u " +
                "JOIN account a ON u.id = a.user_id " +
                "JOIN email_data e ON u.id = e.user_id " +
                "JOIN phone_data p ON u.id = p.user_id " +
                "WHERE u.id = ?";
        UserData userData = jdbcTemplate.query(sql, getExtractor(), event.getUserId());
        if (userData != null) {
            userDataRepository.save(userData);
            log.debug("Update user with id {} in Elasticsearch index", userData.getId());
        } else {
            log.warn("User with id {} not found", event.getUserId());
        }
    }

    private static ResultSetExtractor<UserData> getExtractor() {
        return rs -> {
            UserData userData = null;
            Set<UserData.Phone> phones = new HashSet<>();
            Set<UserData.Email> emails = new HashSet<>();
            while (rs.next()) {
                if (userData == null) {
                    userData = new UserData();
                    userData.setId(rs.getLong("id"));
                    userData.setName(rs.getString("name"));
                    userData.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                    userData.setAccount(UserData.Account.builder()
                            .id(userData.getId())
                            .balance(rs.getBigDecimal("balance"))
                            .build());
                }
                phones.add(UserData.Phone.builder()
                        .id(rs.getLong("phone_id"))
                        .number(rs.getString("phone"))
                        .build());
                emails.add(UserData.Email.builder()
                        .id(rs.getLong("email_id"))
                        .email(rs.getString("email"))
                        .build());
            }

            if (userData != null) {
                userData.setEmails(emails.stream().sorted(Comparator.comparing(UserData.Email::getId)).collect(Collectors.toList()));
                userData.setPhones(phones.stream().sorted(Comparator.comparing(UserData.Phone::getId)).collect(Collectors.toList()));
            }
            return userData;
        };
    }
}
