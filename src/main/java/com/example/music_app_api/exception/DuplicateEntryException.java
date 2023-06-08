package com.example.music_app_api.exception;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(Throwable cause) {
        super(cause);
    }
}
