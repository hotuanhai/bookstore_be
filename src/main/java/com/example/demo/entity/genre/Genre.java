package com.example.demo.entity.genre;

import com.example.demo.entity.book.Book;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "main_genre_id", nullable = false)
    private MainGenre mainGenre;

    @ManyToMany(mappedBy = "genres",fetch = FetchType.LAZY)
    private Set<Book> books;
}
