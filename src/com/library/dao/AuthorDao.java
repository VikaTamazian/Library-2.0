package com.library.dao;

import com.library.entity.Author;
import com.library.entity.Genre;
import com.library.exception.DaoException;
import com.library.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDao implements AbsolutDao<Integer, Author> {

    public static final AuthorDao INSTANCE = new AuthorDao();

    public static final String DELETE_SQL = """
            DELETE  FROM author
            WHERE id = ?
            """;

    public static final String SAVE_SQL = """
            INSERT INTO author(author_name)
            VALUES (?)
            """;

    public static final String UPDATE_SQL = """
            UPDATE author 
            SET author_name = ?
            WHERE id = ?
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id, author_name
            FROM author
            """;

    public static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    public static final String CHECK_UNIQUE_SQL = """
            SELECT author_name
            FROM author 
            WHERE author_name = ?
            """;

    private AuthorDao() {
    }

    public static AuthorDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Author save(Author author) {

        if (checkUnique(author)) {
            try (var connection = ConnectionManager.get();
                 var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, author.getAuthorName());
                preparedStatement.setInt(1, author.getId());
                preparedStatement.executeUpdate();

                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    author.setId(generatedKeys.getInt("id"));
                }
                return author;
            } catch (SQLException e) {
                throw new DaoException(e);
            }

        } else {
            return author;
        }

    }

    @Override
    public void update(Author author) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, author.getAuthorName());
            preparedStatement.setInt(2, author.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Optional<Author> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Author> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Author author = null;
            if (resultSet.next()) {
                author = buildAuthor(resultSet);
            }
            return Optional.ofNullable(author);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Author buildAuthor(java.sql.ResultSet resultSet) throws SQLException {
        return new Author(
                resultSet.getInt("id"),
                resultSet.getString("author_name")
        );
    }

    @Override
    public List<Author> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Author> authors = new ArrayList<>();

            while (resultSet.next()) {
                authors.add(buildAuthor(resultSet));
            }

            return authors;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private boolean checkUnique(Author author) {

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(CHECK_UNIQUE_SQL)) {
            preparedStatement.setString(1, author.getAuthorName());

            var resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }
}
