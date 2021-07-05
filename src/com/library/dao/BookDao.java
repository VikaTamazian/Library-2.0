package com.library.dao;

import com.library.dto.BookFilter;
import com.library.entity.Book;
import com.library.entity.Genre;
import com.library.exception.DaoException;
import com.library.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class BookDao implements AbsolutDao<Integer, Book> {
    public static final BookDao INSTANCE = new BookDao();
    private static final AuthorDao authorDao = AuthorDao.getInstance();
    private static final GenreDao genreDao = GenreDao.getInstance();

    public static final String DELETE_SQL = """
            DELETE FROM book
            WHERE id = ?
            """;
    public static final String SAVE_SQL = """
            INSERT INTO book(name, author_id, genre_id, isbn)
            VALUES (?,?,?,?)
            """;
    public static final String UPDATE_SQL = """
            UPDATE book 
            SET name = ?,
            author_id = ?,
            genre_id = ?,
            isbn = ?
            WHERE id = ?
            """;
    public static final String FIND_ALL_SQL = """
            SELECT id, name, author_id, genre_id, isbn
            FROM book
            """;
    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?  
            """;

    public static final String CHECK_UNIQUE_SQL = """
            SELECT name
            FROM book JOIN author a on a.id = book.author_id
            WHERE name AND author_name = ?
            """;

    private BookDao() {

    }

    public static BookDao getInstance() {
        return INSTANCE;
    }

    public List<Book> findAll(BookFilter bookFilter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (bookFilter.authorId() != null) {
            whereSql.add("WHERE author_id LIKE ?");
            parameters.add("%" + bookFilter.authorId() + "%");
        }
        if (bookFilter.genreId() != null) {
            whereSql.add("WHERE genre_id LIKE ?");
            parameters.add("%" + bookFilter.genreId() + "%");
        }
        parameters.add(bookFilter.limit());
        parameters.add(bookFilter.offset());

        var where = whereSql.stream()
                .collect(joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));

        var sql = FIND_ALL_SQL + where;
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            var resultSet = preparedStatement.executeQuery();
            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(buildBook(resultSet));
            }
            return books;

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public List<Book> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {

                books.add(buildBook(resultSet));

            }
            return books;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Book> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Book book = null;
            if (resultSet.next()) {
                book = buildBook(resultSet);
            }
            return Optional.ofNullable(book);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    private Book buildBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                authorDao.findById(resultSet.getInt("author_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                genreDao.findById(resultSet.getInt("genre_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getString("isbn")
        );
    }

    public void update(Book book) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, book.getName());
            preparedStatement.setInt(2, book.getAuthorId().getId());
            preparedStatement.setInt(3, book.getGenreId().getId());
            preparedStatement.setString(4, book.getIsbn());
            preparedStatement.setInt(5, book.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Book save(Book book) {

        if (checkUnique(book)) {
            try (var connection = ConnectionManager.get();
                 var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, book.getName());
                preparedStatement.setInt(2, book.getAuthorId().getId());
                preparedStatement.setInt(3, book.getGenreId().getId());
                preparedStatement.setString(4, book.getIsbn());
                preparedStatement.executeUpdate();

                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt("id"));
                }
                return book;

            } catch (SQLException e) {
                throw new DaoException(e);
            }
        } else {
            return book;
        }
    }

    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private boolean checkUnique(Book book) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(CHECK_UNIQUE_SQL)) {
            preparedStatement.setString(1, book.getName());
            preparedStatement.setInt(2, book.getAuthorId().getId());

            var resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
