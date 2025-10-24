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

    @Builder.Default
    private int discountPercentage = 0;

    @Builder.Default
    private LocalDateTime discountStartDate = null;

    @Builder.Default
    private LocalDateTime discountEndDate = null;

//    private LocalDateTime printDate;

    @Enumerated(EnumType.STRING)
    private BookStatus status;
}
