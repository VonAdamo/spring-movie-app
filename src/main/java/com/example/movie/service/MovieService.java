package com.example.movie.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie.dto.CreateMovieDTO;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.UpdateMovieDTO;
import com.example.movie.entity.Movie;
import com.example.movie.exception.DuplicateMovieException;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.mapper.MovieMapper;
import com.example.movie.repository.MovieRepository;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
            .stream()
            .map(MovieMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        return MovieMapper.toDto(movie);
    }

    public MovieDTO createMovie(CreateMovieDTO dto) {
        if (movieRepository.existsByTitleAndReleaseDate(dto.getTitle(), dto.getReleaseDate())) {
            throw new DuplicateMovieException(dto.getTitle(), dto.getReleaseDate());
        }
        Movie movie = MovieMapper.toEntity(dto);
        Movie saved = movieRepository.save(movie);
        return MovieMapper.toDto(saved);
    }

    public MovieDTO updateMovie(Long id, UpdateMovieDTO dto) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        if (movieRepository.existsByTitleAndReleaseDate(dto.getTitle(), dto.getReleaseDate())
            && (!dto.getTitle().equals(movie.getTitle())
                || !dto.getReleaseDate().equals(movie.getReleaseDate()))) {
            throw new DuplicateMovieException(dto.getTitle(), dto.getReleaseDate());
        }
        MovieMapper.updateEntityFromDto(dto, movie);
        Movie saved = movieRepository.save(movie);
        return MovieMapper.toDto(saved);
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie", id);
        }
        movieRepository.deleteById(id);
    }
}
