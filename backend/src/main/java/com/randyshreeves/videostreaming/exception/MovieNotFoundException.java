package com.randyshreeves.videostreaming.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long id) {
        super("Movie not found.");
    }
}
