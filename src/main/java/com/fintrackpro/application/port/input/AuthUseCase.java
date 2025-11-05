package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.User;

public interface AuthUseCase {

    User register(User user);

    User authenticate(String email, String password);

    void verifyEmail(String token);

}
