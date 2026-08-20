package com.app.nexora.iam.user.application.port.in;

import com.app.nexora.iam.user.application.model.UserView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public interface CreateUserUseCase {

    UserView create(Command command);

    record Command(
            @NotBlank @Email @Size(max = 320) String email,
            @Size(max = 20) String phone,
            String username,
            @NotBlank @Size(max = 150) String fullName,
            @Size(max = 150) String displayName,
            String avatarUrl,
            @Size(max = 20) String locale,
            @Size(max = 50) String timezone,
            Map<String, Object> metadata
    ) {
    }
}
