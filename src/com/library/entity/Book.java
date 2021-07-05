package com.library.entity;

public class Book {
    private Integer id;
    private String name;
    private Author authorId;
    private Genre genreId;
    private String isbn;

    public Book(Integer id, String name, Author authorId, Genre genreId, String isbn) {
        this.id = id;
        this.name = name;
        this.authorId = authorId;
        this.genreId = genreId;
        this.isbn = isbn;
    }

    public Book() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Author authorId) {
        this.authorId = authorId;
    }

    public Genre getGenreId() {
        return genreId;
    }

    public void setGenreId(Genre genreId) {
        this.genreId = genreId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Book{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", authorId=" + authorId +
               ", genreId=" + genreId +
               ", isbn='" + isbn + '\'' +
               '}';
    }
}
