package com.example.demo.dao;

import com.example.demo.entity.genre.MainGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainGenreRepository extends JpaRepository<MainGenre,Long> {
    Optional<MainGenre> findByName(String name);
}
