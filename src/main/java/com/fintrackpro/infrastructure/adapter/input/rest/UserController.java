package com.fintrackpro.infrastructure.adapter.input.rest;


import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.application.port.input.UserUseCase;
import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateUserPreferencesRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateUserProfileRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing the authenticated user's profile and preferences")
public class UserController {

    private final UserUseCase userUseCase;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        Long userId = currentUserProvider.getCurrentUserId();
        User user = userUseCase.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", toProfileResponse(user)));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        User updated = userUseCase.updateProfile(
                userId,
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.dateOfBirth(),
                request.profilePictureUrl()
        );

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", toProfileResponse(updated)));
    }

    @Operation(summary = "Update current user preferences")
    @PutMapping("/me/preferences")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updatePreferences(
            @Valid @RequestBody UpdateUserPreferencesRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        User updated = userUseCase.updatePreferences(
                userId,
                request.defaultCurrency(),
                request.timezone(),
                request.language()
        );

        return ResponseEntity.ok(ApiResponse.success("Preferences updated successfully", toProfileResponse(updated)));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .phoneNumber(user.phoneNumber())
                .dateOfBirth(user.dateOfBirth())
                .profilePictureUrl(user.profilePictureUrl())
                .defaultCurrency(user.defaultCurrency())
                .timezone(user.timezone())
                .language(user.language())
                .emailVerified(user.emailVerified())
                .build();
    }
}
