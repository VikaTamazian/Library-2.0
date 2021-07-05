package com.library.dto;

public record BookFilter(int limit,      // starting with java 14
                         int offset,
                         Integer authorId,
                         Integer genreId) {
}
