package com.example.movie.external.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbCreditsResponse {

    private List<TmdbCrewMember> crew;

    public TmdbCreditsResponse() {
    }

    public List<TmdbCrewMember> getCrew() {
        return crew;
    }

    public void setCrew(List<TmdbCrewMember> crew) {
        this.crew = crew;
    }
}
