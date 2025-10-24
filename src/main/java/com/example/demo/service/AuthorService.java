package com.example.demo.service;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.AuthorSummaryDto;
import com.example.demo.entity.Author;
import com.example.demo.mapper.AuthorMapper;
import com.example.demo.request.AuthorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthorService{
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional
    public AuthorDto addAuthor(AuthorRequest request){
        Author author = Author.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .nationality(request.getNationality())
                .bookEditions(new HashSet<>())
                .build();

        Author saved = authorRepository.save(author);
        return authorMapper.toDto(saved);
    }

    @Transactional
    public AuthorDto updateAuthor(Long id, AuthorRequest request){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setName(request.getName());
        author.setDescription(request.getDescription());
        author.setImageUrl(request.getImageUrl());
        author.setNationality(request.getNationality());

        Author saved = authorRepository.save(author);
        return authorMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<AuthorSummaryDto> searchAuthors(String keyword, Pageable pageable) {
        Page<Author> authors = authorRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return authors.map(authorMapper::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<AuthorSummaryDto> getAllAuthors(Pageable pageable){
        Page<Author> authors = authorRepository.findAll(pageable);
        return authors.map(authorMapper::toSummaryDto);
    }

}
