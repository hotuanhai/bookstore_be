package com.example.demo.service;

import com.example.demo.dao.GenreRepository;
import com.example.demo.dao.MainGenreRepository;
import com.example.demo.dto.genre.GenreDto;
import com.example.demo.dto.MainGenreDto;
import com.example.demo.entity.genre.Genre;
import com.example.demo.entity.genre.MainGenre;
import com.example.demo.mapper.GenreMapper;
import com.example.demo.mapper.MainGenreMapper;
import com.example.demo.request.GenreRequest;
import com.example.demo.request.MainGenreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainGenreService {
    private final MainGenreRepository mainGenreRepository;
    private final GenreRepository genreRepository;
    private final GenreService genreService;
    private final MainGenreMapper mainGenreMapper;
    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    public MainGenreDto getMainGenreById(Long id) {
        MainGenre mainGenre = mainGenreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + id));
        return mainGenreMapper.toDto(mainGenre);
    }

    @Transactional
    public MainGenreDto addMainGenre(MainGenreRequest request){
        String name = request.getName();
        Optional<MainGenre> existing = mainGenreRepository.findByName(name);
        if (existing.isPresent()) {
            throw new RuntimeException("MainGenre with name '" + name + "' already exists");
        }

        MainGenre mainGenre = new MainGenre();
        mainGenre.setName(name);
        MainGenre saved = mainGenreRepository.save(mainGenre);
        return mainGenreMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public MainGenreDto getMainGenreByName(String name) {
        MainGenre mainGenre = mainGenreRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("MainGenre not found"));
        return mainGenreMapper.toDto(mainGenre);
    }

    @Transactional
    public void deleteMainGenre(Long id){
        MainGenre mainGenre = mainGenreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + id));
        mainGenreRepository.delete(mainGenre);
    }
    @Transactional
    public MainGenreDto renameMainGenre(Long id, MainGenreRequest request){
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }
        Optional<MainGenre> existing = mainGenreRepository.findByName(name);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Main genre name '" + name + "' already exists");
        }

        MainGenre mainGenre = mainGenreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + id));
        if (!mainGenre.getName().equals(name)) {
            mainGenre.setName(name);
        }

        MainGenre saved = mainGenreRepository.save(mainGenre);
        return mainGenreMapper.toDto(mainGenre);
    }

    @Transactional
    public GenreDto addGenreToMainGenre(Long id, GenreRequest request){
        MainGenre mainGenre = mainGenreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MainGenre not found with id: " + id));

        GenreDto genreDto = genreService.addGenre(id, request);
        Genre genre = genreRepository.findById(genreDto.getId())
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));

        mainGenre.getGenres().add(genre);
        return genreMapper.toDto(genre);
    }

    @Transactional(readOnly = true)
    public List<MainGenreDto> getAllMainGenre(){
        return mainGenreRepository.findAll().stream()
                .map(mainGenreMapper::toDto)
                .toList();
    }
}
