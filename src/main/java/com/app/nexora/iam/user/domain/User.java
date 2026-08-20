package com.app.nexora.iam.user.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Pure domain model: no HTTP, Spring or persistence annotations. */
public record User(
        UUID id,
        String email,
        String phone,
        String username,
        String passwordHash,
        String fullName,
        String displayName,
        String avatarUrl,
        String status,
        Instant emailVerifiedAt,
        Instant phoneVerifiedAt,
        Instant lastLoginAt,
        int failedLoginCount,
        Instant lockedUntil,
        String locale,
        String timezone,
        Map<String, Object> metadata,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy,
        Instant deletedAt,
        UUID deletedBy,
        long version
) {

    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_LOCALE = "vi-VN";
    public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";

    public User {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        email = requireText(email, "email").toLowerCase(Locale.ROOT);
        fullName = requireText(fullName, "fullName");
        phone = trimToNull(phone);
        username = trimToNull(username);
        displayName = trimToNull(displayName);
        avatarUrl = trimToNull(avatarUrl);
        status = requireText(status, "status");
        locale = defaultIfBlank(locale, DEFAULT_LOCALE);
        timezone = defaultIfBlank(timezone, DEFAULT_TIMEZONE);
        metadata = metadata == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));

        if (failedLoginCount < 0) {
            throw new IllegalArgumentException("failedLoginCount must not be negative");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative");
        }
    }

    public static User create(
            String email,
            String phone,
            String username,
            String fullName,
            String displayName,
            String avatarUrl,
            String locale,
            String timezone,
            Map<String, Object> metadata,
            Clock clock
    ) {
        Instant now = Instant.now(clock);
        return new User(
                UUID.randomUUID(), email, phone, username, null, fullName, displayName, avatarUrl,
                DEFAULT_STATUS, null, null, null, 0, null, locale, timezone, metadata,
                now, null, now, null, null, null, 0
        );
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        String normalized = trimToNull(value);
        return normalized == null ? defaultValue : normalized;
    }
}
