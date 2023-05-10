package com.benzol45.library.exception;


public class IncorrectDataFromClientException extends RuntimeException{
    public IncorrectDataFromClientException() {
        super();
    }

    public IncorrectDataFromClientException(String message) {
        super(message);
    }

    public IncorrectDataFromClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectDataFromClientException(Throwable cause) {
        super(cause);
    }
}
