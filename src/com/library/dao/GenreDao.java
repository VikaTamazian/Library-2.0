package com.library.dao;

import com.library.entity.Author;
import com.library.entity.Genre;
import com.library.exception.DaoException;
import com.library.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenreDao implements AbsolutDao<Integer, Genre> {
    public static final GenreDao INSTANCE = new GenreDao();

    public static final String DELETE_SQL = """
            DELETE  FROM genre
            WHERE id = ?
            """;

    public static final String SAVE_SQL = """
            INSERT INTO genre(genre_name)
            VALUES (?)
            """;

    public static final String UPDATE_SQL = """
            UPDATE genre 
            SET genre_name = ?
            WHERE id = ?
            """;
    public static final String FIND_ALL_SQL = """
            SELECT id, genre_name
            FROM genre
            """;

    public static final String FIND_BY_ID_SQL = """
            %sWHERE id = ?""".formatted(FIND_ALL_SQL);

    public static final String CHECK_UNIQUE_SQL = """
            SELECT genre_name
            FROM genre 
            WHERE genre_name = ?
            """;


    private GenreDao() {
    }

    public static GenreDao getInstance() {
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
    public Genre save(Genre genre) {

        if (checkUnique(genre)) {
            try (var connection = ConnectionManager.get();
                 var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, genre.getGenreName());
                preparedStatement.setInt(1, genre.getId());
                preparedStatement.executeUpdate();

                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    genre.setId(generatedKeys.getInt("id"));
                }
                return genre;
            } catch (SQLException e) {
                throw new DaoException(e);
            }
        }
        return genre;
    }

    @Override
    public void update(Genre genre) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, genre.getGenreName());
            preparedStatement.setInt(2, genre.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Optional<Genre> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Genre> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Genre genre = null;
            if (resultSet.next()) {
                genre = buildGenre(resultSet);
            }
            return Optional.ofNullable(genre);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Genre buildGenre(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("genre_name")
        );
    }

    @Override
    public List<Genre> findAll() {

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Genre> genres = new ArrayList<>();

            while (resultSet.next()) {
                genres.add(buildGenre(resultSet));
            }

            return genres;

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    private boolean checkUnique(Genre genre) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(CHECK_UNIQUE_SQL)) {
            preparedStatement.setString(1, genre.getGenreName());

            var resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }
}
