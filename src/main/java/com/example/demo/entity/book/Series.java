package com.example.demo.entity.book;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL)
    private Set<Book> books = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addBook(Book book) {
        books.add(book);
        book.setSeries(this);
    }

    public void removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
            book.setSeries(null);
        }
    }
}
