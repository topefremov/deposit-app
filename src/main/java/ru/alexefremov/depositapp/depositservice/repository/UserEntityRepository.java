package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alexefremov.depositapp.depositservice.entity.UserEntity;

import java.util.List;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    @Query("select u.id from UserEntity u")
    List<Long> findAllUsersIds();
}
