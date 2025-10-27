package com.example.demo.dao;

import com.example.demo.dto.BookSummaryDto;
import com.example.demo.entity.Author;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.genre.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.title,
                r.price,
                r.imageUrl,
                r.status,
                r.discountPercentage,
                r.discountStartDate,
                r.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN e.reprints r
            JOIN e.authors a
            WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND b.status NOT IN ('ARCHIVED', 'DELETED')
            """)
    Page<BookSummaryDto> searchByTitleOrAuthor(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.title,
                r.price,
                r.imageUrl,
                r.status,
                r.discountPercentage,
                r.discountStartDate,
                r.discountEndDate
            )
            FROM Book b
            JOIN b.genres g
            JOIN b.editions e
            JOIN e.reprints r
            WHERE g.id = :genreId
              AND b.status NOT IN ('ARCHIVED', 'DELETED')
            """)
    Page<BookSummaryDto> findBooksByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.title,
                r.price,
                r.imageUrl,
                r.status,
                r.discountPercentage,
                r.discountStartDate,
                r.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN e.authors a
            JOIN e.reprints r
            WHERE a.id = :authorId
              AND b.status NOT IN ('ARCHIVED', 'DELETED')
            """)
    Page<BookSummaryDto> findBooksByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.title,
                r.price,
                r.imageUrl,
                r.status,
                r.discountPercentage,
                r.discountStartDate,
                r.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN e.reprints r
            WHERE r.discountEndDate >= :now
            AND r.status = 'AVAILABLE'
            """)
    Page<BookSummaryDto> findBooksWithActiveDiscount(@Param("now") LocalDateTime now, Pageable pageable);

}
