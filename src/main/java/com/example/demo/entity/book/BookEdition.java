package com.example.demo.entity.book;

import com.example.demo.enums.BookStatus;
import com.example.demo.enums.EditionFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEdition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String isbn;

    private String publisher;

    private int publishedYear;

    @Lob
    private String description;

    private String language;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    private String imageUrl;

    private int discountPercentage;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EditionFormat format;

    @Enumerated(EnumType.STRING)
    private BookStatus status;
}
