package ru.alexefremov.depositapp.depositservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.entity.PhoneDataEntity;
import ru.alexefremov.depositapp.depositservice.entity.UserEntity;
import ru.alexefremov.depositapp.depositservice.exception.BusinessLayerException;
import ru.alexefremov.depositapp.depositservice.repository.PhoneDataEntityRepository;
import ru.alexefremov.depositapp.depositservice.repository.UserEntityRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneService {

    private final UserEntityRepository usersRepository;
    private final PhoneDataEntityRepository phonesRepository;

    public Phone addPhone(long userId, String number) {
        Objects.requireNonNull(number, "number is null");
        if (phonesRepository.existsByPhone(number)) {
            throw new BusinessLayerException("Phone " + number + " is already taken");
        }

        PhoneDataEntity phoneData = new PhoneDataEntity();
        phoneData.setPhone(number);
        Optional<UserEntity> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BusinessLayerException("User with id " + userId + " not found");
        }
        phoneData.setUser(user.get());
        phonesRepository.save(phoneData);
        return new Phone(phoneData.getId(), phoneData.getPhone());
    }

    public Phone changePhone(long userId, long phoneId, String newNumber) {
        Objects.requireNonNull(newNumber, "newNumber is null");

        PhoneDataEntity oldPhoneDataEntity = phonesRepository.findByIdAndUserId(phoneId, userId)
                .orElseThrow(() -> new BusinessLayerException("The user with id " +  userId +
                        " does not have a phone with id " + phoneId));

        if (oldPhoneDataEntity.getPhone().equals(newNumber)) {
            return new Phone(oldPhoneDataEntity.getId(), oldPhoneDataEntity.getPhone());
        }

        if (phonesRepository.existsByPhone(newNumber)) {
            throw new BusinessLayerException("Phone " + newNumber + " is already taken");
        }

        oldPhoneDataEntity.setPhone(newNumber);

        phonesRepository.save(oldPhoneDataEntity);
        return new Phone(oldPhoneDataEntity.getId(), oldPhoneDataEntity.getPhone());
    }

    public Phone deletePhone(long userId, long phoneId) {
        List<PhoneDataEntity> userPhones = phonesRepository.findByUserIdWithLock(userId);

        // here we acquired lock on users phones

        if (userPhones.size() == 1) {
            throw new BusinessLayerException("Can't delete the last user's phone");
        }

        PhoneDataEntity phoneDataEntity = userPhones.stream().filter(p -> p.getId() == phoneId).findFirst()
                .orElseThrow(() -> new BusinessLayerException("The user with id " + userId +
                        " does not have a phone with id " + phoneId));
        phonesRepository.delete(phoneDataEntity);
        return new Phone(phoneDataEntity.getId(), phoneDataEntity.getPhone());
    }
}
