package com.example.demo.dao;


import com.example.demo.entity.book.BookEdition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookEditionRepository extends JpaRepository<BookEdition, Long> {
    boolean existsByIsbn(String isbn);
}
