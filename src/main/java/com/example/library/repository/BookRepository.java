package com.example.library.repository;

import com.example.library.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> getAllBooks();

    Optional<Book> findById(int id);

    Book saveOrUpdate(Book book);

    boolean deleteById(int id);
}
