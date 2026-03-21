package com.example.movie.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateMovieDTO {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;

    @NotBlank
    @Size(max = 200)
    private String director;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @NotBlank
    @Size(max = 100)
    private String genre;

    public CreateMovieDTO() {
    }

    public CreateMovieDTO(String title, String description, LocalDate releaseDate, String director,
                          Integer durationMinutes, String genre) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.director = director;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
