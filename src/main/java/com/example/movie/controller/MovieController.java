package com.example.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.movie.dto.CreateMovieDTO;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.UpdateMovieDTO;
import com.example.movie.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public String listMovies(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("movie", new CreateMovieDTO());
        return "movies/create";
    }

    @PostMapping("/create")
    public String createMovie(@Valid @ModelAttribute("movie") CreateMovieDTO dto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movies/create";
        }
        movieService.createMovie(dto);
        return "redirect:/movies";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        MovieDTO movie = movieService.getMovieById(id);
        UpdateMovieDTO dto = new UpdateMovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getReleaseDate(),
            movie.getDirector(),
            movie.getDurationMinutes(),
            movie.getGenre()
        );
        model.addAttribute("movie", dto);
        return "movies/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateMovie(@PathVariable Long id,
                              @Valid @ModelAttribute("movie") UpdateMovieDTO dto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movies/edit";
        }
        movieService.updateMovie(id, dto);
        return "redirect:/movies";
    }

    @PostMapping("/{id}/delete")
    public String deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/movies";
    }
}
