package com.example.demo.entity.book;

import com.example.demo.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "edition_id", nullable = false)
    private BookEdition edition;

    @Column(nullable = false)
    private int reprintNo;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    private String imageUrl;

    private String reprintNotes;

    private int discountPercentage;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    @Enumerated(EnumType.STRING)
    private BookStatus status;
}
