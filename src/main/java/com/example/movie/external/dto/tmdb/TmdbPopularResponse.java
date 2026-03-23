package com.example.movie.external.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbPopularResponse {

    private List<TmdbMovieSummary> results;

    public TmdbPopularResponse() {
    }

    public List<TmdbMovieSummary> getResults() {
        return results;
    }

    public void setResults(List<TmdbMovieSummary> results) {
        this.results = results;
    }
}
