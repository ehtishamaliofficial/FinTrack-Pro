package com.fintrackpro.infrastructure.adapter.input.rest;


import com.fintrackpro.application.service.AuthService;
import com.fintrackpro.infrastructure.adapter.input.dto.mapper.UserApiMapper;
import com.fintrackpro.infrastructure.adapter.input.dto.request.LoginRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RefreshTokenRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RegisterRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserApiMapper userApiMapper;

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(userApiMapper.toDomain(registerRequest));
        return ApiResponse.success("Registration successful. Please check your email to verify your account.");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        String ipAddress = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        
        AuthResponse authResponse = authService.login(
                loginRequest.email(),
                loginRequest.password(),
                ipAddress,
                userAgent
        );
        
        return ApiResponse.success("Login successful", authResponse);
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.refreshToken());
        return ApiResponse.success("Token refreshed successfully", authResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.success("Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ApiResponse<?> logoutAll(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        authService.logoutAll(userId);
        return ApiResponse.success("All sessions logged out successfully");
    }

    @GetMapping("/verify-email")
    public ApiResponse<?> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ApiResponse.success("Email verified successfully. Welcome to FinTrack Pro!");
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
