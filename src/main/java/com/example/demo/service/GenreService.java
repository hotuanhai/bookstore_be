package com.example.demo.service;

import com.example.demo.dao.GenreRepository;
import com.example.demo.dao.MainGenreRepository;
import com.example.demo.dto.GenreDto;
import com.example.demo.entity.genre.Genre;
import com.example.demo.entity.genre.MainGenre;
import com.example.demo.mapper.GenreMapper;
import com.example.demo.request.GenreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final MainGenreRepository mainGenreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public GenreDto addGenre(Long mainGenreId, GenreRequest request){
        MainGenre mainGenre = mainGenreRepository.findById(mainGenreId)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + mainGenreId));

        if (genreRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Genre with name '" + request.getName() + "' already exists");
        }

        Genre newGenre = Genre.builder()
                .name(request.getName())
                .mainGenre(mainGenre)
                .books(new HashSet<>())
                .build();

        Genre saved = genreRepository.save(newGenre);
        return genreMapper.toDto(saved);
    }

    @Transactional
    public Set<GenreDto> getAllGenres() {
        List<Genre> dtos = genreRepository.findAll();
        //alphabetical order
        return dtos.stream()
                .sorted(Comparator.comparing(Genre::getName))
                .map(genreMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional
    public void deleteGenre(Long id){
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found " + id));

        //delete in manytomany
        genre.getBooks().forEach(book -> book.getGenres().remove(genre));
        genre.getBooks().clear();

        genreRepository.delete(genre);
    }

    @Transactional
    public GenreDto renameGenre(Long id, GenreRequest request) {
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }

        Optional<Genre> existing = genreRepository.findByName(name);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Genre name '" + name + "' already exists");
        }

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        if (!genre.getName().equals(name)) {
            genre.setName(name);
        }

        Genre saved = genreRepository.save(genre);
        return genreMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Set<GenreDto> findGenreByMainGenreId(Long id){
        MainGenre mainGenre = mainGenreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + id));
        Set<Genre> genres = genreRepository.findByMainGenre_Id(id);
        return genres.stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void moveGenre(Long genreId, Long targetMainGenreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        MainGenre targetMainGenre = mainGenreRepository.findById(targetMainGenreId)
                .orElseThrow(() -> new RuntimeException("Target MainGenre not found"));

        MainGenre oldMainGenre = genre.getMainGenre();
        oldMainGenre.getGenres().remove(genre);

        genre.setMainGenre(targetMainGenre);
        targetMainGenre.getGenres().add(genre);

        genreRepository.save(genre);
    }

}
