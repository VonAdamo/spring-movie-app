package com.example.movie.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.movie.entity.Movie;
import com.example.movie.external.ExternalMovieApiClient;
import com.example.movie.external.dto.MovieApiResponseDTO;
import com.example.movie.repository.MovieRepository;
import jakarta.validation.ConstraintViolationException;

@Service
public class MovieImportService {

    private static final Logger log = LoggerFactory.getLogger(MovieImportService.class);

    private final ExternalMovieApiClient externalMovieApiClient;
    private final MovieRepository movieRepository;

    public MovieImportService(ExternalMovieApiClient externalMovieApiClient,
                              MovieRepository movieRepository) {
        this.externalMovieApiClient = externalMovieApiClient;
        this.movieRepository = movieRepository;
    }

    public void importMovies() {
        List<MovieApiResponseDTO> apiMovies = externalMovieApiClient.fetchMovies();
        int imported = 0;
        int skipped = 0;
        for (MovieApiResponseDTO apiMovie : apiMovies) {
            if (apiMovie == null || isBlank(apiMovie.getTitle())) {
                skipped++;
                continue;
            }
            Movie movie = new Movie();
            movie.setTitle(apiMovie.getTitle().trim());

            LocalDate releaseDate = parseDate(apiMovie.getReleaseDate());
            if (releaseDate == null) {
                skipped++;
                continue;
            }
            if (releaseDate.isAfter(LocalDate.now())) {
                log.warn("Skipping future release date: title='{}', releaseDate={}",
                    movie.getTitle(), releaseDate);
                skipped++;
                continue;
            }
            if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), releaseDate)) {
                skipped++;
                continue;
            }

            movie.setReleaseDate(releaseDate);
            movie.setDescription(fallbackText(apiMovie.getOverview(), "No description available."));
            movie.setDirector(fallbackText(apiMovie.getDirector(), "Unknown"));
            Integer runtime = apiMovie.getRuntimeMinutes();
            if (runtime == null || runtime <= 0) {
                skipped++;
                continue;
            }
            movie.setDurationMinutes(runtime);
            movie.setGenre(fallbackText(apiMovie.getGenre(), "Unknown"));
            movie.setPosterPath(apiMovie.getPosterPath());
            movie.setBackdropPath(apiMovie.getBackdropPath());
            try {
                movieRepository.save(movie);
                imported++;
            } catch (ConstraintViolationException ex) {
                log.warn("Skipping invalid movie: title='{}' releaseDate='{}' error='{}'",
                    movie.getTitle(), movie.getReleaseDate(), ex.getMessage());
                skipped++;
            }
        }
        log.info("Import finished: imported={}, skipped={}", imported, skipped);
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(date);
        } catch (java.time.format.DateTimeParseException ex) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String fallbackText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
