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
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Common query fragments as compile-time constants
    String BOOK_SUMMARY_SELECT = """
            SELECT DISTINCT new com.example.demo.dto.BookSummaryDto(
                b.id, e.id, b.title, e.imageUrl, e.price, b.status,
                e.discountPercentage, e.discountStartDate, e.discountEndDate
            )
            """;

    String MIN_PRICE_CONDITION = "AND e.price = (SELECT MIN(e2.price) FROM Book b2 JOIN b2.editions e2 WHERE b2.id = b.id)";

    String STATUS_FILTER_AND = " AND b.status NOT IN ('ARCHIVED', 'DELETED') ";

    String STATUS_FILTER_WHERE = " b.status NOT IN ('ARCHIVED', 'DELETED') ";

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """ + STATUS_FILTER_AND + " " + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> searchByTitleOrAuthor(@Param("keyword") String keyword, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """ + STATUS_FILTER_AND + """
              AND LOWER(a.nationality) IN :nationality
            """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> searchByTitleOrAuthorAndCountry(
            @Param("keyword") String keyword,
            @Param("nationality") Set<String> nationality,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
        FROM Book b
        JOIN b.editions e
        JOIN b.authors a
        WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """ + STATUS_FILTER_AND + """
          AND (
              SELECT COUNT(DISTINCT g.id)
              FROM Book b2
              JOIN b2.genres g
              WHERE b2.id = b.id AND g.id IN :genreIds
          ) = :genreCount
        """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> searchByTitleOrAuthorAndGenres(
            @Param("keyword") String keyword,
            @Param("genreIds") Set<Long> genreIds,
            @Param("genreCount") long genreCount,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
        FROM Book b
        JOIN b.editions e
        JOIN b.authors a
        WHERE """ + STATUS_FILTER_WHERE + """
          AND LOWER(a.nationality) IN :nationality
          AND (
              SELECT COUNT(DISTINCT g.id)
              FROM Book b2
              JOIN b2.genres g
              WHERE b2.id = b.id AND g.id IN :genreIds
          ) = :genreCount
        """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findByCountryAndAllGenres(
            @Param("nationality") Set<String> nationality,
            @Param("genreIds") Set<Long> genreIds,
            @Param("genreCount") long genreCount,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
        FROM Book b
        JOIN b.editions e
        JOIN b.authors a
        WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """ + STATUS_FILTER_AND + """
          AND LOWER(a.nationality) IN :nationality
          AND (
              SELECT COUNT(DISTINCT g.id)
              FROM Book b2
              JOIN b2.genres g
              WHERE b2.id = b.id AND g.id IN :genreIds
          ) = :genreCount
        """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> searchByTitleOrAuthorAndCountryAndGenres(
            @Param("keyword") String keyword,
            @Param("nationality") Set<String> nationality,
            @Param("genreIds") Set<Long> genreIds,
            @Param("genreCount") long genreCount,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
        FROM Book b
        JOIN b.editions e
        WHERE """ + STATUS_FILTER_WHERE + """
          AND (
              SELECT COUNT(DISTINCT g.id)
              FROM Book b2
              JOIN b2.genres g
              WHERE b2.id = b.id AND g.id IN :genreIds
          ) = :genreCount
        """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findByAllGenres(
            @Param("genreIds") Set<Long> genreIds,
            @Param("genreCount") long genreCount,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
        FROM Book b
        JOIN b.editions e
        JOIN b.authors a
        WHERE """ + STATUS_FILTER_WHERE + """
          AND LOWER(a.nationality) IN :nationality
        """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findByCountry(
            @Param("nationality") Set<String> nationality,
            Pageable pageable
    );

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            JOIN b.genres g
            WHERE g.id = :genreId
            """ + STATUS_FILTER_AND + " " + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findByGenres_Id(@Param("genreId") Long genreId, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE a.id = :authorId
            """ + STATUS_FILTER_AND + " " + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findBooksByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            WHERE e.discountEndDate >= :now
              AND b.status = 'AVAILABLE'
            """ + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findBooksWithActiveDiscount(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            WHERE """ + STATUS_FILTER_WHERE + " " + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findAllBookSummaries(Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            WHERE b.createdAt >= :fromDate
            """ + STATUS_FILTER_AND + " " + MIN_PRICE_CONDITION)
    Page<BookSummaryDto> findLatestBooks(@Param("fromDate") LocalDateTime fromDate, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            JOIN b.authors a
            WHERE """ + STATUS_FILTER_WHERE + """
              AND LOWER(a.nationality) IN :nationality
              AND b.createdAt >= :fromDate
            """ + MIN_PRICE_CONDITION)
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

    @EntityGraph(attributePaths = {"authors", "genres", "editions", "series"})
    @Query("SELECT b FROM Book b")
    Page<Book> findAllWithDetails(Pageable pageable);

    @EntityGraph(attributePaths = {"authors", "genres", "editions", "series"})
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findAllWithDetailsByTitle(@Param("title") String title, Pageable pageable);

    @Query(BOOK_SUMMARY_SELECT + """
            FROM Book b
            JOIN b.editions e
            WHERE e.id IN :editionIds
            """ + STATUS_FILTER_AND)
    List<BookSummaryDto> findBooksByEditionIds(@Param("editionIds") Set<Long> editionIds);
}