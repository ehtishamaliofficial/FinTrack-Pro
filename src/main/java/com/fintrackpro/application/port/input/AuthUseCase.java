package com.fintrackpro.application.port.input;

import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AuthResponse;

public interface AuthUseCase {

    User register(User user);

    AuthResponse login(String email, String password, String ipAddress, String userAgent);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    void logoutAll(Long userId);

    void verifyEmail(String token);

}
