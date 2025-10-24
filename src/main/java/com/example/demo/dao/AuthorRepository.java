package com.example.demo.dao;

import com.example.demo.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Page<Author> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
