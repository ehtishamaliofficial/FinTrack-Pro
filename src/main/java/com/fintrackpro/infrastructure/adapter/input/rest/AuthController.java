package com.fintrackpro.infrastructure.adapter.input.rest;


import com.fintrackpro.application.service.AuthService;
import com.fintrackpro.infrastructure.adapter.input.dto.mapper.UserApiMapper;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RegisterRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/verify-email")
    public ApiResponse<?> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ApiResponse.success("Email verified successfully. Welcome to FinTrack Pro!");
    }
}
