package com.example.demo.controller;

import com.example.demo.dto.ProfileDto;
import com.example.demo.request.UpdateProfileRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.entity.user.User;
import com.example.demo.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileDto>> getMyProfile(
            @AuthenticationPrincipal User user) {
        ProfileDto profile = profileService.getProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfileById(
            @PathVariable Long profileId) {
        ProfileDto profile = profileService.getProfileById(profileId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfileByUserId(
            @PathVariable Long userId) {
        ProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileDto>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileDto profile = profileService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @AuthenticationPrincipal User user) {
        profileService.deleteProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Profile deleted successfully"));
    }
}