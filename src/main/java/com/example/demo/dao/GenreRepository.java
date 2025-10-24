package com.example.demo.dao;

import com.example.demo.entity.genre.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Set<Genre> findByMainGenre_Id(Long mainGenreId);

    Optional<Genre> findByName(String name);

    boolean existsByName(String name);
}
