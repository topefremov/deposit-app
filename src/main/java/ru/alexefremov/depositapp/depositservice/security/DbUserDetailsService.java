package ru.alexefremov.depositapp.depositservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.entity.EmailDataEntity;
import ru.alexefremov.depositapp.depositservice.entity.PhoneDataEntity;
import ru.alexefremov.depositapp.depositservice.entity.UserEntity;
import ru.alexefremov.depositapp.depositservice.repository.EmailDataEntityRepository;
import ru.alexefremov.depositapp.depositservice.repository.PhoneDataEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final PhoneDataEntityRepository phonesRepository;
    private final EmailDataEntityRepository emailsRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity;
        if (isNumeric(username)) {
            userEntity = phonesRepository.findByPhone(username).map(PhoneDataEntity::getUser);
        } else {
            userEntity = emailsRepository.findByEmail(username).map(EmailDataEntity::getUser);
        }

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User with username " + username + " is not found");
        }
        return userEntity.map(DbUserDetailsService::createUserDetails).get();

    }

    private static UserDetails createUserDetails(UserEntity userEntity) {
        return User.builder()
                .username(userEntity.getId().toString())
                .password(userEntity.getPassword())
                .authorities("USER")
                .build();
    }

    private static boolean isNumeric(String value) {
        return value.chars().allMatch(Character::isDigit);
    }
}
