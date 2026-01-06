package com.fintrackpro.infrastructure.adapter.input.rest;


import com.fintrackpro.application.service.AuthService;
import com.fintrackpro.infrastructure.adapter.input.dto.mapper.UserApiMapper;
import com.fintrackpro.infrastructure.adapter.input.dto.request.LoginRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RefreshTokenRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.RegisterRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user registration, login, token management, and email verification")
public class AuthController {

    private final AuthService authService;
    private final UserApiMapper userApiMapper;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and sends a verification email to the provided address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registration successful, verification email sent",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data or email already in use",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/register")
    public ApiResponse<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(userApiMapper.toDomain(registerRequest));
        return ApiResponse.success("Registration successful. Please check your email to verify your account.");
    }

    @Operation(
            summary = "User login",
            description = "Authenticates the user and returns access and refresh tokens along with session details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid login request data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest loginRequest,
            @Parameter(description = "HTTP servlet request used to extract client IP and user agent", hidden = true)
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

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Expired or invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            )
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.refreshToken());
        return ApiResponse.success("Token refreshed successfully", authResponse);
    }

    @Operation(
            summary = "Logout from current session",
            description = "Invalidates the provided refresh token, logging the user out from the current session"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logged out successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token to invalidate",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            )
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.success("Logged out successfully");
    }

    @Operation(
            summary = "Logout from all sessions",
            description = "Logs the user out from all active sessions by invalidating all refresh tokens for the user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All sessions logged out successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/logout-all")
    public ApiResponse<?> logoutAll(
            @Parameter(description = "Spring Security authentication object containing the current user id", hidden = true)
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        authService.logoutAll(userId);
        return ApiResponse.success("All sessions logged out successfully");
    }

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Operation(
            summary = "Verify user email",
            description = "Verifies the user's email address using a verification token and returns a success page with frontend navigation links"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully, HTML success page returned",
                    content = @Content(mediaType = "text/html")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired verification token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(
            @Parameter(description = "Email verification token", required = true)
            @RequestParam("token") String token) {
        authService.verifyEmail(token);
        
        ModelAndView modelAndView = new ModelAndView("verification-success");
        modelAndView.addObject("dashboardUrl", frontendUrl + "/dashboard");
        modelAndView.addObject("loginUrl", frontendUrl + "/login");
        return modelAndView;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
