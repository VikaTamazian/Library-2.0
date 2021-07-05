package com.library.entity;

public class Genre {
    private Integer id;
    private String genreName;

    public Genre(Integer id, String genreName) {
        this.id = id;
        this.genreName = genreName;
    }

    public Genre() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return "Genre{" +
               "id=" + id +
               ", genreName='" + genreName + '\'' +
               '}';
    }
}
