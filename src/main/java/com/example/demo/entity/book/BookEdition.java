package com.example.demo.entity.book;

import com.example.demo.entity.WishlistItem;
import com.example.demo.entity.cart.CartItem;
import com.example.demo.enums.BookStatus;
import com.example.demo.enums.EditionFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    private String imageUrl;

    private double discountPercentage;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EditionFormat format;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "bookEdition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> wishListItems = new ArrayList<>();

    public boolean isDiscountActive() {
        if (discountPercentage == 0) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return (discountStartDate == null || now.isAfter(discountStartDate)) &&
                (discountEndDate == null || now.isBefore(discountEndDate));
    }

    public BigDecimal getDiscountedPrice() {
        if (!isDiscountActive()) {
            return price.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal discountBD = BigDecimal.valueOf(discountPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal discountedPrice = price.subtract(price.multiply(discountBD));

        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCurrentPrice() {
        return isDiscountActive()
                ? getDiscountedPrice()
                : price.setScale(2, RoundingMode.HALF_UP);
    }
}
