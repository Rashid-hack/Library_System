package com.example.library.repository;
import com.example.library.model.Book;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository{
   private int nextId = 0;

   private final Map<Integer, Book> bookDatabase = new LinkedHashMap<>();

    @Override
    public Book saveOrUpdate(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Kitab null ola bilmez!");
        }
        if (book.getId() == 0) {
            book = new Book(++nextId, book.getTitle(), book.getAuthor(), book.getPrice());
        }
        bookDatabase.put(book.getId(), book);
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookDatabase.values());
    }

    @Override
    public boolean deleteById(int id) {
        return bookDatabase.remove(id) != null;
    }

    @Override
    public Optional<Book> findById(int id) {
        return Optional.ofNullable(bookDatabase.get(id));
    }

}
