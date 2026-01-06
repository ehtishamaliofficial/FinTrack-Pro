package com.fintrackpro.infrastructure.security;

import com.fintrackpro.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaRefreshTokenRepository;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JpaUserRepository userRepository;
    private final JpaRefreshTokenRepository refreshTokenRepository;

    @Value("${app.frontend.url:http://192.168.100.152:8080}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // Handle name splitting safely
        String tempFirstName = "User";
        String tempLastName = "";
        if (name != null) {
            String[] parts = name.split(" ", 2);
            tempFirstName = parts[0];
            tempLastName = parts.length > 1 ? parts[1] : "";
        }

        final String firstName = tempFirstName;
        final String lastName = tempLastName;

        log.info("OAuth2 Login Success for email: {}", email);

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Registering new user from OAuth2: {}", email);
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .username(email) // Use email as username for OAuth users initially
                    .firstName(firstName)
                    .lastName(lastName)
                    .password("") // No password for OAuth users
                    .enabled(true)
                    .accountNonLocked(true)
                    .emailVerified(true) // Verified by provider
                    .failedLoginAttempts(0)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return userRepository.save(newUser);
        });

        // Generate Tokens
        String accessToken = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getEmail(), user.getId());

        // Save Refresh Token
        saveRefreshToken(user, refreshToken, request);

        // Redirect to Frontend
        String targetUrl = String.format("%s/oauth2/redirect?accessToken=%s&refreshToken=%s",
                frontendUrl, accessToken, refreshToken);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(UserEntity user, String token, HttpServletRequest request) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration() / 1000))
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
