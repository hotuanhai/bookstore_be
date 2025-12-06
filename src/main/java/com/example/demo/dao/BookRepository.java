package com.example.demo.dao;

import com.example.demo.dto.BookSummaryDto;
import com.example.demo.entity.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND b.status NOT IN ('ARCHIVED', 'DELETED')
              AND e.price = (
                   SELECT MIN(e2.price)
                   FROM Book b2
                   JOIN b2.editions e2
                   WHERE b2.id = b.id
               )
            """)
    Page<BookSummaryDto> searchByTitleOrAuthor(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND b.status NOT IN ('ARCHIVED', 'DELETED')
              AND LOWER(a.nationality) IN :nationality
              AND e.price = (
                       SELECT MIN(e2.price)
                       FROM Book b2
                       JOIN b2.editions e2
                       WHERE b2.id = b.id
                   )
            """)
    Page<BookSummaryDto> searchByTitleOrAuthorAndCountry(
            @Param("keyword") String keyword,
            @Param("nationality") Set<String> nationality,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN b.genres g
            WHERE g.id = :genreId
            AND b.status NOT IN ('ARCHIVED', 'DELETED')
            AND e.price = (
                 SELECT MIN(e2.price)
                 FROM Book b2
                 JOIN b2.editions e2
                 WHERE b2.id = b.id
             )
            """)
    Page<BookSummaryDto> findByGenres_Id(@Param("genreId") Long genreId, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE a.id = :authorId
            AND b.status NOT IN ('ARCHIVED', 'DELETED')
            AND e.price = (
                 SELECT MIN(e2.price)
                 FROM Book b2
                 JOIN b2.editions e2
                 WHERE b2.id = b.id
             )
            """)
    Page<BookSummaryDto> findBooksByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            WHERE e.discountEndDate >= :now
            AND b.status = 'AVAILABLE'
            AND e.price = (
                 SELECT MIN(e2.price)
                 FROM Book b2
                 JOIN b2.editions e2
                 WHERE b2.id = b.id
             )
            """)
    Page<BookSummaryDto> findBooksWithActiveDiscount(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
            )
            FROM Book b
            JOIN b.editions e
            WHERE b.status NOT IN ('ARCHIVED', 'DELETED')
            AND e.price = (
                SELECT MIN(e2.price)
                FROM Book b2
                JOIN b2.editions e2
                WHERE b2.id = b.id
            )
            """)
    Page<BookSummaryDto> findAllBookSummaries(Pageable pageable);

    @Query("""
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id,
                e.id,
                b.title,
                e.imageUrl,
                e.price,
                b.status,
                e.discountPercentage,
                e.discountStartDate,
                e.discountEndDate
                    )
            FROM Book b
            JOIN b.editions e
            WHERE b.createdAt >= :fromDate
            AND b.status NOT IN ('ARCHIVED', 'DELETED')
            AND e.price = (
            SELECT MIN(e2.price)
            FROM Book b2
            JOIN b2.editions e2
            WHERE b2.id = b.id
            )
            """)
    Page<BookSummaryDto> findLatestBooks(@Param("fromDate") LocalDateTime fromDate, Pageable pageable);

    @Query("""
        SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
            b.id,
            e.id,
            b.title,
            e.imageUrl,
            e.price,
            b.status,
            e.discountPercentage,
            e.discountStartDate,
            e.discountEndDate
        )
        FROM Book b
        JOIN b.editions e
        JOIN b.authors a
        WHERE b.status NOT IN ('ARCHIVED', 'DELETED')
        AND LOWER(a.nationality) IN :nationality
        AND b.createdAt >= :fromDate
        AND e.price = (
            SELECT MIN(e2.price)
            FROM Book b2
            JOIN b2.editions e2
            WHERE b2.id = b.id
        )
        """)
    Page<BookSummaryDto> findLatestBooksByCountry(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("nationality") Set<String> nationality,
            Pageable pageable
    );

    @Query("""
                SELECT b FROM Book b
                LEFT JOIN FETCH b.authors a
                LEFT JOIN FETCH b.genres g
                LEFT JOIN FETCH b.editions e
                LEFT JOIN FETCH b.series s
                WHERE b.id = :id
            """)
    Optional<Book> findBookWithRelations(@Param("id") Long id);

//    @Query("""
//        SELECT b FROM Book b
//        LEFT JOIN FETCH b.authors a
//        LEFT JOIN FETCH b.genres g
//        LEFT JOIN FETCH b.editions e
//        LEFT JOIN FETCH b.series s
//    """)
//    Page<Book> findAllWithDetails(Pageable pageable);

    @EntityGraph(attributePaths = {"authors", "genres", "editions", "series"})
    @Query("SELECT b FROM Book b")
    Page<Book> findAllWithDetails(Pageable pageable);
}
