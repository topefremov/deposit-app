package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alexefremov.depositapp.depositservice.entity.EmailDataEntity;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface EmailDataEntityRepository extends JpaRepository<EmailDataEntity, Long> {
    boolean existsByEmail(String email);

    @Query("select email from EmailDataEntity email where email.id = :id and email.user.id = :userId")
    Optional<EmailDataEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select email from EmailDataEntity email where email.user.id = :userId")
    List<EmailDataEntity> findByUserIdWithLock(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<EmailDataEntity> findByEmail(String email);
}