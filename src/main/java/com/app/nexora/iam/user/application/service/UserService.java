package com.app.nexora.iam.user.application.service;

import com.app.nexora.iam.user.application.model.UserView;
import com.app.nexora.iam.user.application.port.in.CreateUserUseCase;
import com.app.nexora.iam.user.application.port.in.GetAllUsersUseCase;
import com.app.nexora.iam.user.application.port.in.GetUserUseCase;
import com.app.nexora.iam.user.application.port.out.UserRepository;
import com.app.nexora.iam.user.domain.User;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserService implements CreateUserUseCase, GetUserUseCase, GetAllUsersUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public UserService(UserRepository userRepository, Clock clock) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public UserView create(Command command) {
        Objects.requireNonNull(command, "command must not be null");

        User user = User.create(
                command.email(), command.phone(), command.username(), command.fullName(),
                command.displayName(), command.avatarUrl(), command.locale(), command.timezone(),
                command.metadata(), clock
        );
        return UserView.from(userRepository.create(user));
    }

    @Override
    public UserView getById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return userRepository.findById(id)
                .map(UserView::from)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<UserView> getAll() {
        return userRepository.findAll().stream()
                .map(UserView::from)
                .toList();
    }
}
