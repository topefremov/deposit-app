package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexefremov.depositapp.depositservice.entity.EmailDataEntity;

public interface EmailDataEntityRepository extends JpaRepository<EmailDataEntity, Long> {
}