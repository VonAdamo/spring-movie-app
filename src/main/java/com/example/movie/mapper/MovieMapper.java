package com.example.movie.mapper;

import com.example.movie.dto.CreateMovieDTO;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.UpdateMovieDTO;
import com.example.movie.entity.Movie;

public class MovieMapper {

    public static Movie toEntity(CreateMovieDTO dto) {
        if (dto == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDirector(dto.getDirector());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setGenre(dto.getGenre());
        return movie;
    }

    public static void updateEntityFromDto(UpdateMovieDTO dto, Movie movie) {
        if (dto == null || movie == null) {
            return;
        }
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDirector(dto.getDirector());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setGenre(dto.getGenre());
    }

    public static MovieDTO toDto(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getReleaseDate(),
            movie.getDirector(),
            movie.getDurationMinutes(),
            movie.getGenre()
        );
    }
}
