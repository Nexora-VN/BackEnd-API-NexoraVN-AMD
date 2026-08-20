package com.app.nexora.iam.user.web;

import com.app.nexora.iam.user.application.model.UserView;
import com.app.nexora.iam.user.application.port.in.CreateUserUseCase;
import com.app.nexora.iam.user.application.port.in.GetAllUsersUseCase;
import com.app.nexora.iam.user.application.port.in.GetUserUseCase;
import com.app.nexora.iam.user.application.port.in.GetUserUseCase.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@Import(UserExceptionHandler.class)
class UserApiControllerContractTests {

    private static final UUID USER_ID = UUID.fromString("6d73ebea-bbc7-43a7-bd52-1c3c6e3bc924");
    private static final Instant NOW = Instant.parse("2026-08-20T03:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private GetUserUseCase getUserUseCase;

    @MockitoBean
    private GetAllUsersUseCase getAllUsersUseCase;

    @Test
    void createsUserWithoutChangingHttpContract() throws Exception {
        when(createUserUseCase.create(any(CreateUserUseCase.Command.class))).thenReturn(userView());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "dev@nexora.vn",
                                  "username": "dev",
                                  "fullName": "Nexora Developer",
                                  "metadata": {"source": "api"}
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/users/" + USER_ID))
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.email").value("dev@nexora.vn"))
                .andExpect(jsonPath("$.fullName").value("Nexora Developer"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.metadata.source").value("api"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void getsUserById() throws Exception {
        when(getUserUseCase.getById(USER_ID)).thenReturn(userView());

        mockMvc.perform(get("/api/v1/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.email").value("dev@nexora.vn"));
    }

    @Test
    void getsAllUsers() throws Exception {
        when(getAllUsersUseCase.getAll()).thenReturn(List.of(userView()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID.toString()))
                .andExpect(jsonPath("$[0].email").value("dev@nexora.vn"));
    }

    @Test
    void returnsNotFoundProblemDetail() throws Exception {
        when(getUserUseCase.getById(USER_ID)).thenThrow(new UserNotFoundException(USER_ID));

        mockMvc.perform(get("/api/v1/users/{id}", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User not found"))
                .andExpect(jsonPath("$.detail").value("User not found: " + USER_ID));
    }

    private static UserView userView() {
        return new UserView(
                USER_ID, "dev@nexora.vn", null, "dev", "Nexora Developer", null,
                null, "ACTIVE", null, null, "vi-VN", "Asia/Ho_Chi_Minh",
                Map.of("source", "api"), NOW, NOW, 0
        );
    }
}
