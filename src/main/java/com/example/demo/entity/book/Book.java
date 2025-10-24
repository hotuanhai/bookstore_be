package com.example.demo.entity.book;

import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookEdition> editions = new HashSet<>();


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
