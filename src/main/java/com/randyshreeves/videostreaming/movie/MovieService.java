package com.randyshreeves.videostreaming.movie;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovie(Long id) {
        return movieRepository.findById(id).orElseThrow();
    }

    public Movie updateMovie(Long id, Movie movie) {
        Movie existingMovie = movieRepository.findById(id).orElseThrow();
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setReleaseYear(movie.getReleaseYear());
        existingMovie.setRuntimeMinutes(movie.getRuntimeMinutes());
        existingMovie.setStorageLocation(movie.getStorageLocation());
        return movieRepository.save(existingMovie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
