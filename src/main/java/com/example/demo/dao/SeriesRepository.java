package com.example.demo.dao;

import com.example.demo.entity.book.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    boolean existsByName(String name);
}
