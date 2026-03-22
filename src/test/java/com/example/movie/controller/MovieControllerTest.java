package com.example.movie.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.movie.dto.MovieDTO;
import com.example.movie.service.MovieService;

@WebMvcTest(MovieController.class)
@Import(GlobalExceptionHandler.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    void contextLoads() {
        // minimal smoke test
    }

    @Test
    void getMovies_returnsListViewWithModel() throws Exception {
        MovieDTO first = new MovieDTO(
            1L,
            "Inception",
            "A mind-bending thriller.",
            LocalDate.of(2010, 7, 16),
            "Christopher Nolan",
            148,
            "Sci-Fi"
        );
        MovieDTO second = new MovieDTO(
            2L,
            "The Matrix",
            "A hacker discovers reality.",
            LocalDate.of(1999, 3, 31),
            "The Wachowskis",
            136,
            "Sci-Fi"
        );

        when(movieService.getAllMovies()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/movies"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies/list"))
            .andExpect(model().attributeExists("movies"));
    }

    @Test
    void getCreateForm_returnsCreateViewWithModel() throws Exception {
        mockMvc.perform(get("/movies/create"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies/create"))
            .andExpect(model().attributeExists("movie"));
    }

    @Test
    void postCreateMovie_withValidData_redirects() throws Exception {
        MovieDTO created = new MovieDTO(
            1L,
            "Inception",
            "A mind-bending thriller.",
            LocalDate.of(2010, 7, 16),
            "Christopher Nolan",
            148,
            "Sci-Fi"
        );

        when(movieService.createMovie(org.mockito.ArgumentMatchers.any()))
            .thenReturn(created);

        mockMvc.perform(post("/movies/create")
                .param("title", "Inception")
                .param("description", "A mind-bending thriller.")
                .param("releaseDate", "2010-07-16")
                .param("director", "Christopher Nolan")
                .param("durationMinutes", "148")
                .param("genre", "Sci-Fi"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(movieService).createMovie(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void postCreateMovie_withInvalidData_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/movies/create")
                .param("title", "")
                .param("description", "A mind-bending thriller.")
                .param("releaseDate", "2010-07-16")
                .param("director", "Christopher Nolan")
                .param("durationMinutes", "0")
                .param("genre", "Sci-Fi"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies/create"))
            .andExpect(model().attributeHasFieldErrors("movie", "title", "durationMinutes"));

        verify(movieService, never()).createMovie(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getEditForm_returnsEditViewWithModel() throws Exception {
        MovieDTO existing = new MovieDTO(
            1L,
            "Inception",
            "A mind-bending thriller.",
            LocalDate.of(2010, 7, 16),
            "Christopher Nolan",
            148,
            "Sci-Fi"
        );

        when(movieService.getMovieById(1L)).thenReturn(existing);

        mockMvc.perform(get("/movies/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies/edit"))
            .andExpect(model().attributeExists("movie"));
    }

    @Test
    void postEditMovie_withValidData_redirects() throws Exception {
        MovieDTO updated = new MovieDTO(
            1L,
            "Inception",
            "Updated description.",
            LocalDate.of(2010, 7, 16),
            "Christopher Nolan",
            148,
            "Sci-Fi"
        );

        when(movieService.updateMovie(org.mockito.ArgumentMatchers.eq(1L),
            org.mockito.ArgumentMatchers.any()))
            .thenReturn(updated);

        mockMvc.perform(post("/movies/1/edit")
                .param("id", "1")
                .param("title", "Inception")
                .param("description", "Updated description.")
                .param("releaseDate", "2010-07-16")
                .param("director", "Christopher Nolan")
                .param("durationMinutes", "148")
                .param("genre", "Sci-Fi"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(movieService).updateMovie(org.mockito.ArgumentMatchers.eq(1L),
            org.mockito.ArgumentMatchers.any());
    }

    @Test
    void postEditMovie_withInvalidData_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/movies/1/edit")
                .param("id", "1")
                .param("title", "")
                .param("description", "Updated description.")
                .param("releaseDate", "2010-07-16")
                .param("director", "Christopher Nolan")
                .param("durationMinutes", "0")
                .param("genre", "Sci-Fi"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies/edit"))
            .andExpect(model().attributeHasFieldErrors("movie", "title", "durationMinutes"));

        verify(movieService, never()).updateMovie(org.mockito.ArgumentMatchers.anyLong(),
            org.mockito.ArgumentMatchers.any());
    }

    @Test
    void postDeleteMovie_redirects() throws Exception {
        mockMvc.perform(post("/movies/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(movieService).deleteMovie(1L);
    }

    @Test
    void getEditForm_whenNotFound_returnsErrorView() throws Exception {
        when(movieService.getMovieById(999L))
            .thenThrow(new com.example.movie.exception.ResourceNotFoundException("Movie", 999L));

        mockMvc.perform(get("/movies/999/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postCreateMovie_whenDuplicate_returnsErrorView() throws Exception {
        when(movieService.createMovie(org.mockito.ArgumentMatchers.any()))
            .thenThrow(new com.example.movie.exception.DuplicateMovieException(
                "Inception",
                java.time.LocalDate.of(2010, 7, 16)
            ));

        mockMvc.perform(post("/movies/create")
                .param("title", "Inception")
                .param("description", "A mind-bending thriller.")
                .param("releaseDate", "2010-07-16")
                .param("director", "Christopher Nolan")
                .param("durationMinutes", "148")
                .param("genre", "Sci-Fi"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postDeleteMovie_whenNotFound_returnsErrorView() throws Exception {
        org.mockito.Mockito.doThrow(new com.example.movie.exception.ResourceNotFoundException("Movie", 999L))
            .when(movieService).deleteMovie(999L);

        mockMvc.perform(post("/movies/999/delete"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attributeExists("errorMessage"));
    }
}
