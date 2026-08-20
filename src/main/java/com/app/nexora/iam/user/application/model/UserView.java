package com.app.nexora.iam.user.application.model;

import com.app.nexora.iam.user.domain.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserView(
        UUID id,
        String email,
        String phone,
        String username,
        String fullName,
        String displayName,
        String avatarUrl,
        String status,
        Instant emailVerifiedAt,
        Instant phoneVerifiedAt,
        String locale,
        String timezone,
        Map<String, Object> metadata,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public static UserView from(User user) {
        return new UserView(
                user.id(), user.email(), user.phone(), user.username(), user.fullName(),
                user.displayName(), user.avatarUrl(), user.status(), user.emailVerifiedAt(),
                user.phoneVerifiedAt(), user.locale(), user.timezone(), user.metadata(),
                user.createdAt(), user.updatedAt(), user.version()
        );
    }
}
