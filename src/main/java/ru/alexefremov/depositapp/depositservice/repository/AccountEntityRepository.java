package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alexefremov.depositapp.depositservice.entity.AccountEntity;

import javax.persistence.LockModeType;
import java.util.List;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {
    @Query("select a.id from AccountEntity a")
    List<Long> findAllAccountsIds();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    AccountEntity findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity  a where a.user.id = :userId")
    AccountEntity findByUserIdWithLock(@Param("userId") Long userId);
}