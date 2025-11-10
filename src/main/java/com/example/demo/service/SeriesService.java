package com.example.demo.service;

import com.example.demo.dao.BookRepository;
import com.example.demo.dao.SeriesRepository;
import com.example.demo.dto.SeriesDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.Series;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.SeriesNotFoundException;
import com.example.demo.mapper.SeriesMapper;
import com.example.demo.request.SeriesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final BookRepository bookRepository;
    private final SeriesMapper seriesMapper;

    @Transactional
    public SeriesDto createSeries(SeriesRequest request) {
        Series series = Series.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Set<Book> books = new HashSet<>();
        for (Long bookId : request.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book not found with id " + bookId));
            books.add(book);
            book.setSeries(series);
        }
        series.setBooks(books);

        Series saved = seriesRepository.save(series);
        return seriesMapper.toDto(saved);
    }

    @Transactional
    public SeriesDto updateSeries(Long id, SeriesRequest request) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found with id " + id));

        series.setName(request.getName());
        series.setDescription(request.getDescription());

        // Ensure bidirectional
        if (series.getBooks() != null) {
            series.getBooks().forEach(book -> book.setSeries(series));
        }

        Series saved = seriesRepository.save(series);
        return seriesMapper.toDto(saved);
    }

    @Transactional
    public void deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Series not found with id " + id));

        series.getBooks().forEach(book -> book.setSeries(null));
        seriesRepository.delete(series);
    }

    @Transactional
    public SeriesDto addBooksToSeries(Long seriesId, Set<Long> bookIds) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found with id " + seriesId));

        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book not found with id " + bookId));
            series.addBook(book);
        }

        Series saved = seriesRepository.save(series);
        return seriesMapper.toDto(saved);
    }

    @Transactional
    public SeriesDto removeBookFromSeries(Long seriesId, Long bookId) {
        // just check, never happen
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found with id " + seriesId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id " + bookId));
        if (!book.getSeries().getId().equals(seriesId)) {
            throw new IllegalArgumentException("Book does not belong to this series");
        }

        series.removeBook(book);

        Series saved = seriesRepository.save(series);
        return seriesMapper.toDto(saved);
    }
}
