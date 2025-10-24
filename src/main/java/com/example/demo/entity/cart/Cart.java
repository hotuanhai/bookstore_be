package com.example.demo.entity.cart;

import com.example.demo.entity.book.Book;
import com.example.demo.entity.user.User;
import com.example.demo.enums.CartStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_id")
    private String sessionId; // For anonymous users

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_amount", precision = 10, scale = 2)
    private int totalAmount = 0;

    @Column(name = "total_items")
    private int totalItems = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CartStatus status = CartStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Constructors
    public Cart() {}

    public Cart(User user) {
        this.user = user;
        this.expiresAt = LocalDateTime.now().plusDays(30); // Cart expires in 30 days
    }

    public Cart(String sessionId) {
        this.sessionId = sessionId;
        this.expiresAt = LocalDateTime.now().plusDays(7); // Anonymous cart expires in 7 days
    }

    // Business methods
    public void addItem(Book book, Integer quantity) {
        CartItem existingItem = findItemByBook(book);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.updateSubtotal();
        } else {
            CartItem newItem = new CartItem(this, book, quantity);
            items.add(newItem);
        }

        updateTotals();
    }

    public void removeItem(Book book) {
        items.removeIf(item -> item.getBook().getId().equals(book.getId()));
        updateTotals();
    }

    public void updateItemQuantity(Book book, Integer quantity) {
        CartItem item = findItemByBook(book);
        if (item != null) {
            if (quantity <= 0) {
                removeItem(book);
            } else {
                item.setQuantity(quantity);
                item.updateSubtotal();
                updateTotals();
            }
        }
    }

    public void clearCart() {
        items.clear();
        updateTotals();
    }

    public void updateTotals() {
        this.totalAmount = items.stream()
                .mapToInt(CartItem::getSubtotal)
                .sum();

        this.totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    private CartItem findItemByBook(Book book) {
        return items.stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public void extendExpiration() {
        if (user != null) {
            this.expiresAt = LocalDateTime.now().plusDays(30);
        } else {
            this.expiresAt = LocalDateTime.now().plusDays(7);
        }
    }
}
