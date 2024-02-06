package ru.alexefremov.depositapp.depositservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.entity.EmailDataEntity;
import ru.alexefremov.depositapp.depositservice.entity.UserEntity;
import ru.alexefremov.depositapp.depositservice.exception.BusinessLayerException;
import ru.alexefremov.depositapp.depositservice.repository.EmailDataEntityRepository;
import ru.alexefremov.depositapp.depositservice.repository.UserEntityRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final UserEntityRepository usersRepository;
    private final EmailDataEntityRepository emailsRepository;

    public Email addEmail(long userId, String emailValue) {
        Objects.requireNonNull(emailValue, "emailValue is null");
        if (emailsRepository.existsByEmail(emailValue)) {
            throw new BusinessLayerException("Email " + emailValue + " is already taken");
        }

        EmailDataEntity emailData = new EmailDataEntity();
        emailData.setEmail(emailValue);
        Optional<UserEntity> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BusinessLayerException("User with id " + userId + " not found");
        }
        emailData.setUser(user.get());
        emailsRepository.save(emailData);
        return new Email(emailData.getId(), emailData.getEmail());
    }

    public Email changeEmail(long userId, long emailId, String newEmail) {
        Objects.requireNonNull(newEmail, "newEmail is null");

        EmailDataEntity oldEmailDataEntity = emailsRepository.findByIdAndUserId(emailId, userId)
                .orElseThrow(() -> new BusinessLayerException("The user with id " +  userId +
                        " does not have an email with id " + emailId));

        if (oldEmailDataEntity.getEmail().equals(newEmail)) {
            return new Email(oldEmailDataEntity.getId(), oldEmailDataEntity.getEmail());
        }

        if (emailsRepository.existsByEmail(newEmail)) {
            throw new BusinessLayerException("Email " + newEmail + " is already taken");
        }

        oldEmailDataEntity.setEmail(newEmail);

        emailsRepository.save(oldEmailDataEntity);
        return new Email(oldEmailDataEntity.getId(), oldEmailDataEntity.getEmail());
    }

    public Email deleteEmail(long userId, long emailId) {
        List<EmailDataEntity> userEmails = emailsRepository.findByUserIdWithLock(userId);

        // here we acquired lock on users emails

        if (userEmails.size() == 1) {
            throw new BusinessLayerException("Can't delete the last user's email");
        }

        EmailDataEntity emailDataEntity = userEmails.stream().filter(p -> p.getId() == emailId).findFirst()
                .orElseThrow(() -> new BusinessLayerException("The user with id " + userId +
                        " does not have an email with id " + emailId));
        emailsRepository.delete(emailDataEntity);
        return new Email(emailDataEntity.getId(), emailDataEntity.getEmail());
    }
}
