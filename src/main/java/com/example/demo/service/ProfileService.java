package com.example.demo.service;

import com.example.demo.dao.ProfileRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.ProfileDto;
import com.example.demo.entity.user.Profile;
import com.example.demo.entity.user.User;
import com.example.demo.exception.ProfileNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.request.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public Profile getOrCreateProfile(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found"));

                    Profile profile = Profile.builder()
                            .user(user)
                            .displayName(user.getUsername())
                            .build();

                    user.setProfile(profile);
                    userRepository.save(user);
                    return profile;
                });
    }

    @Transactional
    public ProfileDto getProfile(Long userId) {
        Profile profile = getOrCreateProfile(userId);
        return toDto(profile);
    }

    @Transactional(readOnly = true)
    public ProfileDto getProfileById(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        return toDto(profile);
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUser_Id(userId)
                .orElseGet(() -> getOrCreateProfile(userId));

        // Update only non-null fields
        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getFacebookUrl() != null) {
            profile.setFacebookUrl(request.getFacebookUrl());
        }

        userRepository.save(profile.getUser());
        return toDto(profile);
    }

    @Transactional
    public void deleteProfile(Long userId) {
        Profile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        profileRepository.delete(profile);
    }

    // helper method
    public ProfileDto toDto(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .dateOfBirth(profile.getDateOfBirth())
                .location(profile.getLocation())
                .facebookUrl(profile.getFacebookUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
