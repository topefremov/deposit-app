package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexefremov.depositapp.depositservice.entity.AccountEntity;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {
}