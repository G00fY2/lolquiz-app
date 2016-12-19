package com.g00fy2.lolquiz.exception;

public class ApiException extends Exception {

    public ApiException(String message)
    {
        super(message);
    }

    public ApiException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
