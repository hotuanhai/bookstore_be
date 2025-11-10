package com.example.demo.entity.book;

import com.example.demo.entity.Author;
import com.example.demo.entity.Comment;
import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookEdition> editions = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

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

    public void addEdition(BookEdition edition) {
        if (editions == null) {
            editions = new HashSet<>();
        }
        editions.add(edition);      // Parent → Child
        edition.setBook(this);      // Child → Parent
    }
    public void removeEdition(BookEdition edition) {
        editions.remove(edition);
        edition.setBook(null);
    }
}
