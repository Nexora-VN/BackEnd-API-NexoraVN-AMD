package com.app.nexora.iam.user.application.service;

import com.app.nexora.iam.user.application.model.UserView;
import com.app.nexora.iam.user.application.port.in.CreateUserUseCase.Command;
import com.app.nexora.iam.user.application.port.in.GetUserUseCase.UserNotFoundException;
import com.app.nexora.iam.user.application.port.out.UserRepository;
import com.app.nexora.iam.user.domain.User;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTests {

    private static final Instant NOW = Instant.parse("2026-08-20T03:00:00Z");

    private final InMemoryUserRepository repository = new InMemoryUserRepository();
    private final UserService service = new UserService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void createsUserWithSchemaDefaults() {
        UserView created = service.create(new Command(
                "Dev@Nexora.vn", null, "dev", "Nexora Developer", null, null,
                null, null, Map.of("source", "api")
        ));

        assertEquals("dev@nexora.vn", created.email());
        assertEquals(User.DEFAULT_STATUS, created.status());
        assertEquals(User.DEFAULT_LOCALE, created.locale());
        assertEquals(User.DEFAULT_TIMEZONE, created.timezone());
        assertEquals(NOW, created.createdAt());
    }

    @Test
    void getsUserById() {
        UserView created = service.create(new Command(
                "dev@nexora.vn", null, null, "Nexora Developer", null, null,
                null, null, Map.of()
        ));

        assertEquals(created, service.getById(created.id()));
    }

    @Test
    void getsAllUsers() {
        UserView created = service.create(new Command(
                "dev@nexora.vn", null, null, "Nexora Developer", null, null,
                null, null, Map.of()
        ));

        assertEquals(List.of(created), service.getAll());
    }

    @Test
    void throwsWhenUserDoesNotExist() {
        UUID missingId = UUID.randomUUID();

        assertThrows(UserNotFoundException.class, () -> service.getById(missingId));
    }

    private static class InMemoryUserRepository implements UserRepository {

        private final Map<UUID, User> users = new HashMap<>();

        @Override
        public User create(User user) {
            users.put(user.id(), user);
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(users.values());
        }
    }
}
