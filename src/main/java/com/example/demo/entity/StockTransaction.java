package com.example.demo.entity;

import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.user.User;
import com.example.demo.enums.StockReason;
import com.example.demo.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_edition_id", nullable = false)
    private BookEdition bookEdition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // IN or OUT

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int stockBefore;

    @Column(nullable = false)
    private int stockAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockReason reason;

    private String referenceNumber; // Order ID

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy; // Admin/Staff who made the transaction

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    public void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }
}
