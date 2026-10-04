package com.example.library.repository;

import com.example.library.model.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

    public class JdbcBookRepository implements BookRepository {

        private final String url;

        public JdbcBookRepository(String url) {
            this.url = url;
            createTable();
        }

        private Connection connect() throws SQLException {
            return DriverManager.getConnection(url);
        }

        private void createTable() {
            String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id     INT AUTO_INCREMENT PRIMARY KEY,
                    title  VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    price  DOUBLE NOT NULL
                )
                """;
            try (Connection conn = connect();
                 Statement st = conn.createStatement()) {
                st.execute(sql);
            } catch (SQLException e) {
                throw new IllegalStateException("Cedvel yaradila bilmedi", e);
            }
        }

        @Override
        public List<Book> getAllBooks() {
            String sql = "SELECT id, title, author, price FROM books ORDER BY id";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                List<Book> books = new ArrayList<>();
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
                return books;
            } catch (SQLException e) {
                throw new IllegalStateException("Kitablar oxuna bilmedi", e);
            }
        }

        @Override
        public Optional<Book> findById(int id) {
            String sql = "SELECT id, title, author, price FROM books WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapRow(rs));
                    }
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Kitab axtarilarken xeta bas verdi", e);
            }
        }

        @Override
        public Book saveOrUpdate(Book book) {
            if (book == null) {
                throw new IllegalArgumentException("Kitab null ola bilmez!");
            }
            return book.getId() == 0 ? insert(book) : update(book);
        }

        private Book insert(Book book) {
            String sql = "INSERT INTO books (title, author, price) VALUES (?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setDouble(3, book.getPrice());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return new Book(keys.getInt(1), book.getTitle(), book.getAuthor(), book.getPrice());
                    }
                    throw new IllegalStateException("Baza yeni ID qaytarmadi");
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Kitab elave edile bilmedi", e);
            }
        }

        private Book update(Book book) {
            String sql = "UPDATE books SET title = ?, author = ?, price = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setDouble(3, book.getPrice());
                ps.setInt(4, book.getId());

                if (ps.executeUpdate() == 0) {
                    throw new IllegalArgumentException("Bu ID ile kitab yoxdur: " + book.getId());
                }
                return book;
            } catch (SQLException e) {
                throw new IllegalStateException("Kitab yenilene bilmedi", e);
            }
        }

        @Override
        public boolean deleteById(int id) {
            String sql = "DELETE FROM books WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new IllegalStateException("Kitab siline bilmedi", e);
            }
        }

        private Book mapRow(ResultSet rs) throws SQLException {
            return new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"));
        }
    }
