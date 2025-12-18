package com.example.demo.mapper;

import com.example.demo.dto.ProfileDto;
import com.example.demo.dto.security.UserResponseDto;
import com.example.demo.entity.user.Profile;
import com.example.demo.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getUsername())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();

        if (user.getProfile() != null) {
            dto.setProfileDto(toProfileDto(user.getProfile(), user.getId()));
        }

        return dto;
    }

    private ProfileDto toProfileDto(Profile profile, Long userId) {
        ProfileDto dto = ProfileDto.builder()
                .id(profile.getId())
                .userId(userId)
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .build();
        dto.setId(profile.getId());
        dto.setUserId(userId);
        dto.setDisplayName(profile.getDisplayName());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setBio(profile.getBio());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setLocation(profile.getLocation());
        dto.setFacebookUrl(profile.getFacebookUrl());
        return dto;
    }
}
