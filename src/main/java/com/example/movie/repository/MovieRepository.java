package com.example.movie.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.movie.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByDirectorContainingIgnoreCase(String director);

    Page<Movie> findByTitleContainingIgnoreCaseAndDirectorContainingIgnoreCaseAndGenreContainingIgnoreCase(
        String title, String director, String genre, Pageable pageable);
}
