package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexefremov.depositapp.depositservice.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

}
