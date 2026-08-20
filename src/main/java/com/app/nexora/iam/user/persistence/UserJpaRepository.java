package com.app.nexora.iam.user.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByIdAndDeletedAtIsNull(UUID id);

    List<UserJpaEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
}
