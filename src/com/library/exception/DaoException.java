package com.library.exception;

public class DaoException extends RuntimeException {

    public DaoException(Throwable throwable) {
        super(throwable);
        System.out.println("Something went wrong.");
    }

}
