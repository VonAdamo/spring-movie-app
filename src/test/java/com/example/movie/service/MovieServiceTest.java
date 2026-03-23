package com.example.movie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.movie.dto.CreateMovieDTO;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.UpdateMovieDTO;
import com.example.movie.entity.Movie;
import com.example.movie.exception.DuplicateMovieException;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie sampleMovie;
    private CreateMovieDTO createDto;
    private UpdateMovieDTO updateDto;

    @BeforeEach
    void setUp() {
        sampleMovie = new Movie();
        sampleMovie.setId(1L);
        sampleMovie.setTitle("Inception");
        sampleMovie.setDescription("A mind-bending thriller.");
        sampleMovie.setReleaseDate(LocalDate.of(2010, 7, 16));
        sampleMovie.setDirector("Christopher Nolan");
        sampleMovie.setDurationMinutes(148);
        sampleMovie.setGenre("Sci-Fi");

        createDto = new CreateMovieDTO(
            "Interstellar",
            "A space exploration epic.",
            LocalDate.of(2014, 11, 7),
            "Christopher Nolan",
            169,
            "Sci-Fi"
        );

        updateDto = new UpdateMovieDTO(
            1L,
            "Inception",
            "A mind-bending thriller (updated).",
            LocalDate.of(2010, 7, 16),
            "Christopher Nolan",
            148,
            "Sci-Fi"
        );
    }

    @Test
    void createMovie_whenDuplicate_throwsException() {
        when(movieRepository.existsByTitleAndReleaseDate(
            createDto.getTitle(), createDto.getReleaseDate()
        )).thenReturn(true);

        assertThrows(DuplicateMovieException.class, () -> movieService.createMovie(createDto));
    }

    @Test
    void createMovie_whenOk_returnsDto() {
        Movie saved = new Movie();
        saved.setId(2L);
        saved.setTitle(createDto.getTitle());
        saved.setDescription(createDto.getDescription());
        saved.setReleaseDate(createDto.getReleaseDate());
        saved.setDirector(createDto.getDirector());
        saved.setDurationMinutes(createDto.getDurationMinutes());
        saved.setGenre(createDto.getGenre());

        when(movieRepository.existsByTitleAndReleaseDate(
            createDto.getTitle(), createDto.getReleaseDate()
        )).thenReturn(false);
        when(movieRepository.save(org.mockito.ArgumentMatchers.any(Movie.class))).thenReturn(saved);

        MovieDTO result = movieService.createMovie(createDto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(createDto.getTitle(), result.getTitle());
        verify(movieRepository).save(org.mockito.ArgumentMatchers.any(Movie.class));
    }

    @Test
    void getMovieById_whenMissing_throwsException() {
        when(movieRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(99L));
    }

    @Test
    void getMovieById_whenFound_returnsDto() {
        when(movieRepository.findById(1L)).thenReturn(java.util.Optional.of(sampleMovie));

        MovieDTO result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals(sampleMovie.getId(), result.getId());
        assertEquals(sampleMovie.getTitle(), result.getTitle());
    }

    @Test
    void getAllMovies_returnsDtoList() {
        Movie secondMovie = new Movie();
        secondMovie.setId(2L);
        secondMovie.setTitle("The Matrix");
        secondMovie.setDescription("A hacker discovers reality.");
        secondMovie.setReleaseDate(LocalDate.of(1999, 3, 31));
        secondMovie.setDirector("The Wachowskis");
        secondMovie.setDurationMinutes(136);
        secondMovie.setGenre("Sci-Fi");

        when(movieRepository.findAll()).thenReturn(List.of(sampleMovie, secondMovie));

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertEquals(sampleMovie.getId(), result.get(0).getId());
        assertEquals(sampleMovie.getTitle(), result.get(0).getTitle());
        assertEquals(secondMovie.getId(), result.get(1).getId());
        assertEquals(secondMovie.getTitle(), result.get(1).getTitle());
    }

    @Test
    void updateMovie_whenFound_updatesAndReturnsDto() {
        when(movieRepository.findById(1L)).thenReturn(java.util.Optional.of(sampleMovie));
        when(movieRepository.existsByTitleAndReleaseDate(
            updateDto.getTitle(), updateDto.getReleaseDate()
        )).thenReturn(false);
        when(movieRepository.save(sampleMovie)).thenReturn(sampleMovie);

        MovieDTO result = movieService.updateMovie(1L, updateDto);

        assertNotNull(result);
        assertEquals(sampleMovie.getId(), result.getId());
        assertEquals(updateDto.getDescription(), result.getDescription());
        verify(movieRepository).save(sampleMovie);
    }

    @Test
    void updateMovie_whenMissing_throwsException() {
        when(movieRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(1L, updateDto));
    }

    @Test
    void updateMovie_whenDuplicate_throwsException() {
        when(movieRepository.findById(1L)).thenReturn(java.util.Optional.of(sampleMovie));
        UpdateMovieDTO changingDto = new UpdateMovieDTO(
            1L,
            "Interstellar",
            updateDto.getDescription(),
            LocalDate.of(2014, 11, 7),
            updateDto.getDirector(),
            updateDto.getDurationMinutes(),
            updateDto.getGenre()
        );

        when(movieRepository.existsByTitleAndReleaseDate(
            changingDto.getTitle(), changingDto.getReleaseDate()
        )).thenReturn(true);

        assertThrows(DuplicateMovieException.class, () -> movieService.updateMovie(1L, changingDto));
    }

    @Test
    void deleteMovie_whenFound_deletes() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        verify(movieRepository).deleteById(1L);
    }

    @Test
    void deleteMovie_whenMissing_throwsException() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

    @Test
    void getMovies_withFiltersAndPaging_returnsPageDto() {
        Movie secondMovie = new Movie();
        secondMovie.setId(2L);
        secondMovie.setTitle("The Matrix");
        secondMovie.setDescription("A hacker discovers reality.");
        secondMovie.setReleaseDate(LocalDate.of(1999, 3, 31));
        secondMovie.setDirector("The Wachowskis");
        secondMovie.setDurationMinutes(136);
        secondMovie.setGenre("Sci-Fi");

        Pageable pageable = PageRequest.of(0, 2);
        Page<Movie> page = new PageImpl<>(List.of(sampleMovie, secondMovie), pageable, 2);

        when(movieRepository.findByTitleContainingIgnoreCaseAndDirectorContainingIgnoreCaseAndGenreContainingIgnoreCase(
            "Inception", "", "Sci-Fi", pageable)).thenReturn(page);

        Page<MovieDTO> result = movieService.getMovies("Inception", "", "Sci-Fi", 0, 2);

        assertEquals(2, result.getTotalElements());
        assertEquals("Inception", result.getContent().get(0).getTitle());
        assertEquals("The Matrix", result.getContent().get(1).getTitle());
    }
}
