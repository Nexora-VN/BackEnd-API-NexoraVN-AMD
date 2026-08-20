package com.app.nexora.iam.user.application.port.out;

import com.app.nexora.iam.user.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User create(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();
}
