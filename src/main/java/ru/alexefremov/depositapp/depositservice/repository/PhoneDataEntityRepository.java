package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexefremov.depositapp.depositservice.entity.PhoneDataEntity;

public interface PhoneDataEntityRepository extends JpaRepository<PhoneDataEntity, Long> {
}