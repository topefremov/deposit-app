package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alexefremov.depositapp.depositservice.entity.PhoneDataEntity;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PhoneDataEntityRepository extends JpaRepository<PhoneDataEntity, Long> {
    boolean existsByPhone(String phone);

    @Query("select phone from PhoneDataEntity phone where phone.id = :id and phone.user.id = :userId")
    Optional<PhoneDataEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select phone from PhoneDataEntity phone where phone.user.id = :userId")
    List<PhoneDataEntity> findByUserIdWithLock(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<PhoneDataEntity> findByPhone(String phone);
}