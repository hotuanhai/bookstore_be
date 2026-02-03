package com.example.demo.dao;

import com.example.demo.dto.stock.TrendDataPoint;
import com.example.demo.entity.StockTransaction;
import com.example.demo.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByBookEditionIdOrderByTransactionDateDesc(Long editionId);

    @Query("SELECT SUM(st.quantity) FROM StockTransaction st " +
            "WHERE st.bookEdition.id = :editionId AND st.type = :type")
    Integer getTotalQuantityByEditionAndType(
            @Param("editionId") Long editionId,
            @Param("type") TransactionType type);

    // Get total quantity for a specific edition, type, and date range
    @Query("SELECT SUM(st.quantity) FROM StockTransaction st " +
            "WHERE st.bookEdition.id = :editionId AND st.type = :type " +
            "AND st.transactionDate BETWEEN :startDate AND :endDate")
    Integer getTotalQuantityByEditionTypeAndDateRange(
            @Param("editionId") Long editionId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get total quantity for all editions of a book by type
    @Query("SELECT SUM(st.quantity) FROM StockTransaction st " +
            "WHERE st.bookEdition.book.id = :bookId AND st.type = :type")
    Integer getTotalQuantityByBookAndType(
            @Param("bookId") Long bookId,
            @Param("type") TransactionType type);

    // Get total quantity for all editions of a book by type and date range
    @Query("SELECT SUM(st.quantity) FROM StockTransaction st " +
            "WHERE st.bookEdition.book.id = :bookId AND st.type = :type " +
            "AND st.transactionDate BETWEEN :startDate AND :endDate")
    Integer getTotalQuantityByBookTypeAndDateRange(
            @Param("bookId") Long bookId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get total quantity by type across all editions
    @Query("SELECT SUM(st.quantity) FROM StockTransaction st WHERE st.type = :type")
    Integer getTotalQuantityByType(@Param("type") TransactionType type);

    // Get transactions by type and date range
    @Query("SELECT SUM(st.quantity) FROM StockTransaction st " +
            "WHERE st.type = :type AND st.transactionDate BETWEEN :startDate AND :endDate")
    Integer getTotalQuantityByTypeAndDateRange(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
