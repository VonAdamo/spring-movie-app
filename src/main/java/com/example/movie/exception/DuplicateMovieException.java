package com.example.movie.exception;

public class DuplicateMovieException extends RuntimeException {

    public DuplicateMovieException(String title) {
        super("Movie with title '" + title + "' already exists.");
    }

    public DuplicateMovieException(String title, java.time.LocalDate releaseDate) {
        super("Movie with title '" + title + "' and release date " + releaseDate + " already exists.");
    }
}
