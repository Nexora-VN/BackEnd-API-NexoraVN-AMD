package com.app.nexora.iam.user.persistence;

import com.app.nexora.iam.user.application.port.out.UserRepository;
import com.app.nexora.iam.user.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final EntityManager entityManager;

    UserPersistenceAdapter(UserJpaRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public User create(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        entityManager.persist(entity);
        return entity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAllByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }
}
