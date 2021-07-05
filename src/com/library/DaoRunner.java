package com.library;

import com.library.dao.BookDao;
import com.library.dto.BookFilter;
import com.library.entity.Book;


public class DaoRunner {
    public static void main(String[] args) {
        findBookById();
    }

    private static void findBookById() {
        var maybeBook = BookDao.getInstance().findById(2);
        System.out.println(maybeBook);
    }

    private static void findByFilter() {
        var bookFilter = new BookFilter(10, 0, null, 1);
        var bookList = BookDao.getInstance().findAll(bookFilter);
        System.out.println(bookList);
    }

    private static void updateBook() {
        var bookDao = BookDao.getInstance();
        var maybeBook = bookDao.findById(1);
        System.out.println(maybeBook);
        maybeBook.ifPresent(book -> {
            book.setIsbn("00000000");
            bookDao.update(book);
        });
    }

    private static void deleteBook() {
        var bookDao = BookDao.getInstance();
        var deleted = bookDao.delete(1);
        System.out.println(deleted);
    }

    private static void saveBook() {
        var bookDao = BookDao.getInstance();
        var book = new Book();

        book.setName("test");
        //book.setAuthorId(1);
        //book.setGenreId(1);
        book.setIsbn("test");

        var savedBook = bookDao.save(book);
        System.out.println(savedBook);
    }
}
