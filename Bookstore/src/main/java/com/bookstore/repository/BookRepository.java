package com.bookstore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByCategory(String category);
    
    List<Book> findByTitleContaining(String title);
    
    // Additional method using Java 8+ features
    default List<Book> findByCategoryAndTitleContaining(String category, String title) {
        return findByCategory(category).stream()
                .filter(book -> book.getTitle().contains(title))
                .toList();
    }

}