package com.example.library.repository;

import com.example.library.model.Book;
import org.jooq.CloseableDSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class JooqBookRepository implements BookRepository{

    private static final Table<Record> BOOKS = table("books");
    private static final Field<Integer> ID = field("id", Integer.class);
    private static final Field<String> TITLE = field("title", String.class);
    private static final Field<String> AUTHOR = field("author", String.class);
    private static final Field<Double> PRICE = field("price", Double.class);

    private final String url;

    public JooqBookRepository(String url){
        this.url = url;
        createTable();
    }

    private CloseableDSLContext open(){
        return DSL.using(url);
    }

    private void createTable(){
        try(CloseableDSLContext ctx = open()){
            ctx.execute("""
                    CREATE TABLE IF NOT EXISTS books(
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    price DOUBLE NOT NULL
                    )
                    """);
        }
    }

    @Override
    public List<Book> getAllBooks() {
        try(CloseableDSLContext ctx = open()){
            return ctx.select(ID, TITLE, AUTHOR, PRICE)
                    .from(BOOKS)
                    .orderBy(ID)
                    .fetch(JooqBookRepository::toBook);
        }
    }

    @Override
    public Optional<Book> findById(int id){
        try(CloseableDSLContext ctx = open()){
            return ctx.select(ID, TITLE, AUTHOR, PRICE)
                    .from(BOOKS)
                    .where(ID.eq(id))
                    .fetchOptional(JooqBookRepository::toBook);
        }
    }

    @Override
    public Book saveOrUpdate(Book book) {
        if (book == null){
            throw new IllegalArgumentException("Kitab null ola bilmez!");
        }
        return book.getId() == 0 ? insert(book) : update(book);
    }

    private Book insert(Book book) {
        try (CloseableDSLContext ctx = open()) {
            Integer newId = ctx.insertInto(BOOKS, TITLE, AUTHOR, PRICE)
                    .values(book.getTitle(), book.getAuthor(), book.getPrice())
                    .returningResult(ID)
                    .fetchOne(ID);
            return new Book(newId, book.getTitle(), book.getAuthor(), book.getPrice());
        }
    }

    private Book update(Book book) {
        try (CloseableDSLContext ctx = open()) {
            int rows = ctx.update(BOOKS)
                    .set(TITLE, book.getTitle())
                    .set(AUTHOR, book.getAuthor())
                    .set(PRICE, book.getPrice())
                    .where(ID.eq(book.getId()))
                    .execute();
            if (rows == 0) {
                throw new IllegalArgumentException("Bu ID ile kitab yoxdur: " + book.getId());
            }
            return book;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (CloseableDSLContext ctx = open()) {
            return ctx.deleteFrom(BOOKS)
                    .where(ID.eq(id))
                    .execute() > 0;
        }
    }

    private static Book toBook(Record r) {
        return new Book(r.get(ID), r.get(TITLE), r.get(AUTHOR), r.get(PRICE));
    }
}
