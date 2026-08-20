package com.app.nexora.iam.user.web;

import com.app.nexora.iam.user.application.model.UserView;
import com.app.nexora.iam.user.application.port.in.CreateUserUseCase;
import com.app.nexora.iam.user.application.port.in.GetAllUsersUseCase;
import com.app.nexora.iam.user.application.port.in.GetUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
class UserApiController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    UserApiController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
    }

    @GetMapping
    List<UserView> getAll() {
        return getAllUsersUseCase.getAll();
    }

    @GetMapping("/{id}")
    UserView getById(@PathVariable UUID id) {
        return getUserUseCase.getById(id);
    }

    @PostMapping
    ResponseEntity<UserView> create(@Valid @RequestBody CreateUserUseCase.Command command) {
        UserView created = createUserUseCase.create(command);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
    
}
