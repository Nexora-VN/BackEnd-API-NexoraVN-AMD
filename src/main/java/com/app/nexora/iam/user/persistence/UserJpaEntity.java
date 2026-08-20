package com.app.nexora.iam.user.persistence;

import com.app.nexora.iam.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "citext")
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "citext")
    private String username;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Column(name = "avatar_url", columnDefinition = "text")
    private String avatarUrl;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "phone_verified_at")
    private Instant phoneVerifiedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(length = 20)
    private String locale;

    @Column(length = 50)
    private String timezone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Version
    @Column(nullable = false)
    private long version;

    static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(
                user.id(), user.email(), user.phone(), user.username(), user.passwordHash(),
                user.fullName(), user.displayName(), user.avatarUrl(), user.status(),
                user.emailVerifiedAt(), user.phoneVerifiedAt(), user.lastLoginAt(),
                user.failedLoginCount(), user.lockedUntil(), user.locale(), user.timezone(),
                user.metadata(), user.createdAt(), user.createdBy(), user.updatedAt(),
                user.updatedBy(), user.deletedAt(), user.deletedBy(), user.version()
        );
    }

    User toDomain() {
        return new User(
                id, email, phone, username, passwordHash, fullName, displayName, avatarUrl,
                status, emailVerifiedAt, phoneVerifiedAt, lastLoginAt, failedLoginCount,
                lockedUntil, locale, timezone, metadata, createdAt, createdBy, updatedAt,
                updatedBy, deletedAt, deletedBy, version
        );
    }
}
