package com.app.nexora.iam.user.application.port.in;

import com.app.nexora.iam.user.application.model.UserView;

import java.util.UUID;

public interface GetUserUseCase {

    UserView getById(UUID id);

    final class UserNotFoundException extends RuntimeException {

        public UserNotFoundException(UUID id) {
            super("User not found: " + id);
        }
    }
}
