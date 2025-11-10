package com.example.demo.dao;

import com.example.demo.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Page<Author> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
        SELECT DISTINCT a FROM Author a
        LEFT JOIN FETCH a.books b
        LEFT JOIN FETCH b.editions e
        WHERE a.id = :authorId
    """)
    Optional<Author> findAuthorWithBooks(@Param("authorId") Long authorId);
}
