package com.randyshreeves.videostreaming.movie;


import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable Long id) {
        return movieService.getMovie(id);
    }

    @PostMapping
    public MovieResponse createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        return movieService.createMovie(movieRequest);
    }

    @PutMapping("/{id}")
    public MovieResponse updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest movieRequest) {
        return movieService.updateMovie(id, movieRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
