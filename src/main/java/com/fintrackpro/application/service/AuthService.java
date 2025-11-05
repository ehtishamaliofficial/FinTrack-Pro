package com.fintrackpro.application.service;

import com.fintrackpro.application.port.input.AuthUseCase;
import com.fintrackpro.domain.exception.InvalidRequestException;
import com.fintrackpro.domain.model.User;
import com.fintrackpro.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import com.fintrackpro.infrastructure.util.MessageUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;

    @Override
    public User register(User user) {

        if (userRepositoryPort.findByUsername(user.username()).isPresent()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.username.exists"));
        }
        if (userRepositoryPort.findByEmail(user.email()).isPresent()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.email.exists"));
        }
        if (!passwordValidator.validate(new PasswordData(user.password())).isValid()) {
            throw new InvalidRequestException(messageUtil.getMessage("error.password.invalid"));
        }

        user.toBuilder().password(passwordEncoder.encode(user.password())).build();

        return userRepositoryPort.save(user);
    }

    @Override
    public User authenticate(String email, String password) {
        return null;
    }
}
