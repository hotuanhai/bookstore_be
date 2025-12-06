package com.example.demo.entity.cart;

import com.example.demo.entity.book.BookEdition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edition_id", nullable = false)
    private BookEdition edition;

    @NotNull
    @Positive
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    public BigDecimal getSubtotal() {
        return getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getCurrentPrice() {
        if (edition.isDiscountActive()) {
            return edition.getDiscountedPrice().setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(edition.getPrice()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getOriginalPrice() {
        return BigDecimal.valueOf(edition.getPrice()).setScale(2, RoundingMode.HALF_UP);
    }
}