package com.example.demo.dao;

import com.example.demo.entity.user.Profile;
import com.example.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser_Id(Long userId);

    @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithUser(@Param("userId") Long userId);

    boolean existsByUser_Id(Long userId);
}
