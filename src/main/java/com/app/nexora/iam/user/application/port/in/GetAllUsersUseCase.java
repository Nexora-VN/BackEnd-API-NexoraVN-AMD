package com.app.nexora.iam.user.application.port.in;

import com.app.nexora.iam.user.application.model.UserView;

import java.util.List;

public interface GetAllUsersUseCase {

    List<UserView> getAll();
}
