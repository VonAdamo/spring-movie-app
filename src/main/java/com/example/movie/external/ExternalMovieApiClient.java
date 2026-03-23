package com.example.movie.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.movie.external.dto.MovieApiResponseDTO;
import com.example.movie.external.dto.tmdb.TmdbCreditsResponse;
import com.example.movie.external.dto.tmdb.TmdbGenre;
import com.example.movie.external.dto.tmdb.TmdbMovieDetails;
import com.example.movie.external.dto.tmdb.TmdbMovieSummary;
import com.example.movie.external.dto.tmdb.TmdbPopularResponse;

@Component
public class ExternalMovieApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final int pageCount;

    public ExternalMovieApiClient(RestTemplate restTemplate,
                                  @Value("${tmdb.api.base-url:https://api.themoviedb.org}") String baseUrl,
                                  @Value("${tmdb.api.key:}") String apiKey,
                                  @Value("${tmdb.api.page-count:3}") int pageCount) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.pageCount = pageCount;
    }

    public List<MovieApiResponseDTO> fetchMovies() {
        List<MovieApiResponseDTO> movies = new ArrayList<>();
        int pages = Math.max(1, pageCount);
        for (int page = 1; page <= pages; page++) {
            String popularUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/3/movie/popular")
                .queryParam("api_key", apiKey)
                .queryParam("language", "en-US")
                .queryParam("page", page)
                .toUriString();

            TmdbPopularResponse popularResponse;
            try {
                popularResponse = restTemplate.getForObject(popularUrl, TmdbPopularResponse.class);
            } catch (RestClientException ex) {
                continue;
            }
            if (popularResponse == null || popularResponse.getResults() == null) {
                continue;
            }

            for (TmdbMovieSummary summary : popularResponse.getResults()) {
                if (summary.getId() == null) {
                    continue;
                }

                TmdbMovieDetails details = fetchDetails(summary.getId());
                TmdbCreditsResponse credits = fetchCredits(summary.getId());

                String director = findDirector(credits);
                String genre = firstGenre(details);

                MovieApiResponseDTO dto = new MovieApiResponseDTO();
                dto.setTitle(details != null ? details.getTitle() : summary.getTitle());
                dto.setOverview(details != null ? details.getOverview() : summary.getOverview());
                dto.setReleaseDate(details != null ? details.getReleaseDate() : summary.getReleaseDate());
                dto.setRuntimeMinutes(details != null ? details.getRuntime() : null);
                dto.setDirector(director);
                dto.setGenre(genre);
                dto.setPosterPath(details != null && details.getPosterPath() != null
                    ? details.getPosterPath()
                    : summary.getPosterPath());
                dto.setBackdropPath(details != null && details.getBackdropPath() != null
                    ? details.getBackdropPath()
                    : summary.getBackdropPath());
                movies.add(dto);
            }
        }

        return movies;
    }

    private TmdbMovieDetails fetchDetails(Long id) {
        String detailsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/3/movie/{id}")
            .queryParam("api_key", apiKey)
            .buildAndExpand(id)
            .toUriString();
        try {
            return restTemplate.getForObject(detailsUrl, TmdbMovieDetails.class);
        } catch (RestClientException ex) {
            return null;
        }
    }

    private TmdbCreditsResponse fetchCredits(Long id) {
        String creditsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/3/movie/{id}/credits")
            .queryParam("api_key", apiKey)
            .buildAndExpand(id)
            .toUriString();
        try {
            return restTemplate.getForObject(creditsUrl, TmdbCreditsResponse.class);
        } catch (RestClientException ex) {
            return null;
        }
    }

    private String findDirector(TmdbCreditsResponse credits) {
        if (credits == null || credits.getCrew() == null) {
            return null;
        }
        Optional<com.example.movie.external.dto.tmdb.TmdbCrewMember> director = credits.getCrew()
            .stream()
            .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
            .findFirst();
        return director.map(com.example.movie.external.dto.tmdb.TmdbCrewMember::getName).orElse(null);
    }

    private String firstGenre(TmdbMovieDetails details) {
        if (details == null || details.getGenres() == null || details.getGenres().isEmpty()) {
            return null;
        }
        TmdbGenre genre = details.getGenres().get(0);
        return genre != null ? genre.getName() : null;
    }
}
