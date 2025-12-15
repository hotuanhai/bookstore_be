package com.example.demo.entity.order;

import com.example.demo.entity.book.BookEdition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edition_id", nullable = false)
    private BookEdition edition;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    //productName = book name + edition name
    private String productName;

    public BigDecimal getTotalPrice() {
        return priceAtPurchase.multiply(new BigDecimal(quantity));
    }

    public String getProductName() {
        if (productName != null) {
            return productName;
        }

        if (edition == null || edition.getBook() == null) {
            return null;
        }

        return edition.getBook().getTitle() + " - " + edition.getName();
    }
}
