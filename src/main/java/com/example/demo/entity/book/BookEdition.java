package com.example.demo.entity.book;

import com.example.demo.entity.Author;
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
public class BookEdition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "book_edition_author",
            joinColumns = @JoinColumn(name = "edition_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int editionNo;

    private String dimension;

    private int numberOfPages;

    private int publishedYear;

    @Lob
    private String description;

    private String translator;

    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<BookReprint> reprints = new HashSet<>();


    public void addReprint(BookReprint reprint) {
        if (reprints == null) {
            reprints = new HashSet<>();
        }
        reprints.add(reprint);      // Parent → Child
        reprint.setEdition(this);   // Child → Parent
    }
    public void removeReprint(BookReprint reprint) {
        reprints.remove(reprint);
        reprint.setEdition(null);
    }
}
