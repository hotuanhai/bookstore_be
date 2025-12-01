package com.example.demo.service.security;

import com.example.demo.dao.ProfileRepository;
import com.example.demo.entity.user.Profile;
import com.example.demo.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Transactional
    public Profile createProfile(User user) {
        if (user.getProfile() != null) {
            return user.getProfile();
        }

        Profile profile = Profile.builder()
                .user(user)
                .displayName(user.getUsername())
                .avatarUrl(null)
                .bio(null)
                .location(null)
                .facebookUrl(null)
                .dateOfBirth(null)
                .build();
        //bidirectional save
        user.setProfile(profile);

        return profile;
    }
}
