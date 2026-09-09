package com.profile.api.idempotency.repository;

import com.profile.api.idempotency.model.IdempotencyKey;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM IdempotencyKey k WHERE k.idempotencyKey = :key")
    Optional<IdempotencyKey> findByIdempotencyKeyForUpdate(String key);

    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);
}
