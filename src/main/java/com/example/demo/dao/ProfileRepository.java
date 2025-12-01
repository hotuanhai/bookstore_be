package com.example.demo.dao;

import com.example.demo.entity.user.Profile;
import com.example.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
