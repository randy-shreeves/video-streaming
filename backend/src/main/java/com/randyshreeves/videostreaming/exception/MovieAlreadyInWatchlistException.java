package com.randyshreeves.videostreaming.exception;

public class MovieAlreadyInWatchlistException extends RuntimeException {

    public MovieAlreadyInWatchlistException(String message) {
        super(message);
    }
}
