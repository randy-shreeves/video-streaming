package com.randyshreeves.videostreaming.movie;

import com.randyshreeves.videostreaming.exception.MovieNotFoundException;
import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Value("${media.root}")
    private String mediaRoot;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieResponse createMovie(MovieRequest movieRequest) {
        Movie movie = toMovie(movieRequest);
        Movie savedMovie = movieRepository.save(movie);
        return toMovieResponse(savedMovie);
    }

    public List<MovieResponse> getAllMovies() {
        List<MovieResponse> movieResponseList = new ArrayList<>();
        for (Movie movie : movieRepository.findAll()) {
            movieResponseList.add(toMovieResponse(movie));
        }
        return movieResponseList;
    }

    public MovieResponse getMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        return toMovieResponse(movie);
    }

    public Resource getMovieStream(Long id) throws MalformedURLException {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        Path path = Paths.get(mediaRoot, movie.getStorageLocation());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("Video file not found.");
        }
        return resource;
    }

    public MovieResponse updateMovie(Long id, MovieRequest movieRequest) {
        Movie existingMovie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        existingMovie.setTitle(movieRequest.getTitle());
        existingMovie.setDescription(movieRequest.getDescription());
        existingMovie.setReleaseYear(movieRequest.getReleaseYear());
        existingMovie.setRuntimeMinutes(movieRequest.getRuntimeMinutes());
        existingMovie.setStorageLocation(movieRequest.getStorageLocation());
        Movie savedMovie = movieRepository.save(existingMovie);
        return toMovieResponse(savedMovie);
    }

    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        movieRepository.delete(movie);
    }

    private MovieResponse toMovieResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getRuntimeMinutes()
        );
    }

    private Movie toMovie(MovieRequest movieRequest) {
        return new Movie(
          movieRequest.getTitle(),
          movieRequest.getDescription(),
          movieRequest.getReleaseYear(),
          movieRequest.getRuntimeMinutes(),
          movieRequest.getStorageLocation()
        );
    }
}
