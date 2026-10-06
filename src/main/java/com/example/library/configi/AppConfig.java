package com.example.library.configi;

import com.example.library.repository.BookRepository;
import com.example.library.repository.JooqBookRepository;
import com.example.library.service.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookRepository bookRepository() {
        return new JooqBookRepository("jdbc:h2:./data/library");
    }

    @Bean
    public BookService bookService(BookRepository bookRepository) {
        return new BookService(bookRepository);
    }
}
