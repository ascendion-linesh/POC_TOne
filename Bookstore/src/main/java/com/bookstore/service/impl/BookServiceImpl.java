package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(@NonNull BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @NonNull
    public List<Book> findAll() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .filter(Book::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public Book findOne(@NonNull Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
    }

    @Override
    @NonNull
    public List<Book> findByCategory(@NonNull String category) {
        return bookRepository.findByCategory(category)
                .stream()
                .filter(Book::isActive)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<Book> blurrySearch(@NonNull String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .filter(Book::isActive)
                .collect(Collectors.toList());
    }
}
