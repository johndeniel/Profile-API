package com.profile.api.idempotency.service;

import com.profile.api.idempotency.model.IdempotencyKey;
import com.profile.api.idempotency.repository.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdempotencyKeyService {

    private final IdempotencyKeyRepository repository;

    public IdempotencyKeyService(IdempotencyKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Optional<IdempotencyKey> findOrCreate(String idempotencyKey, long ttlSeconds) {
        Optional<IdempotencyKey> existing = repository.findByIdempotencyKeyForUpdate(idempotencyKey);

        if (existing.isPresent()) {
            IdempotencyKey key = existing.get();

            if (!key.isExpired()) {
                if (key.isCompleted()) {
                    return Optional.of(key);
                }
                return Optional.empty();
            }

            repository.delete(key);
            repository.flush();
        }

        IdempotencyKey key = new IdempotencyKey();
        key.setIdempotencyKey(idempotencyKey);
        key.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));
        return Optional.of(repository.save(key));
    }

    @Transactional
    public void markCompleted(IdempotencyKey key, int status, byte[] bodyBytes, String contentType) {
        key.setResponseStatus(status);
        key.setResponseContentType(contentType);
        key.setStatus(IdempotencyKey.Status.COMPLETED);
        if (bodyBytes != null) {
            key.setResponseBody(new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8));
        }
        repository.save(key);
    }

    @Transactional
    public void delete(IdempotencyKey key) {
        repository.delete(key);
    }
}
