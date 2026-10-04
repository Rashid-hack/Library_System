package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {

   private final BookRepository repo;

    public BookService(BookRepository repo){
        this.repo = repo;
    }

    public List<Book> getAllBooks(){
        return repo.getAllBooks();
    }

    public boolean deleteById(int id){
        checkId(id);
        return repo.deleteById(id);
    }

    public Book addBook(String title, String author, double price){
        validatePrice(price);

        String cleanTitle = validateAndTrimText(title, "Kitabin adi bos ola bilmez!");
        String cleanAuthor = validateAndTrimText(author, "Muellifin adi bos ola bilmez!");


        Book newBook = new Book(cleanTitle, cleanAuthor, price);
        return repo.saveOrUpdate(newBook);
    }

    public boolean updatePrice(int id, double newPrice){
        validatePrice(newPrice);
        checkId(id);

        Optional<Book> bookOpt = repo.findById(id);
        if (bookOpt.isPresent()){
            Book book = bookOpt.get();
            book.setPrice(newPrice);
            repo.saveOrUpdate(book);
            return true;
        }
        return false;
    }

    private void validatePrice(double price){
        if (price < 0){
            throw new IllegalArgumentException("Qiymet menfi ola bilmez!");
        }
    }

    private String validateAndTrimText(String text, String errorMessage){
        if (text == null || text.trim().isEmpty()){
            throw new IllegalArgumentException(errorMessage);
        }
        return text.trim();
    }

    private void checkId(int id){
        if ( id <= 0){
            throw new IllegalArgumentException("ID 0 ve ya menfi ola bilmez!");
        }
    }
}
