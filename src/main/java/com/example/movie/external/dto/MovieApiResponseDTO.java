package com.example.movie.external.dto;

public class MovieApiResponseDTO {

    private String title;
    private String overview;
    private String releaseDate;
    private String director;
    private Integer runtimeMinutes;
    private String genre;
    private String posterPath;
    private String backdropPath;

    public MovieApiResponseDTO() {
    }

    public MovieApiResponseDTO(String title, String overview, String releaseDate, String director,
                               Integer runtimeMinutes, String genre, String posterPath, String backdropPath) {
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
        this.genre = genre;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
