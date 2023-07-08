package com.av.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class MessageIdDuplicatedException extends RuntimeException{
    public MessageIdDuplicatedException() {
    }

    public MessageIdDuplicatedException(String message) {
        super(message);
    }

    public MessageIdDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
