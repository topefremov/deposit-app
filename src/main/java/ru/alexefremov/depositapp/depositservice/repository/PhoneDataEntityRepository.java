package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexefremov.depositapp.depositservice.entity.PhoneDataEntity;

import java.util.Optional;

public interface PhoneDataEntityRepository extends JpaRepository<PhoneDataEntity, Long> {
    boolean existsByPhone(String phone);

    Optional<PhoneDataEntity> findByPhone(String phone);
}