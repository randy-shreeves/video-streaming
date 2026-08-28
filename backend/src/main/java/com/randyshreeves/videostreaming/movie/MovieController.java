package com.randyshreeves.videostreaming.movie;

import com.randyshreeves.videostreaming.auth.StreamTokenService;
import com.randyshreeves.videostreaming.movie.dto.MovieRequest;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final StreamTokenService streamTokenService;

    public MovieController(MovieService movieService, StreamTokenService streamTokenService) {
        this.movieService = movieService;
        this.streamTokenService = streamTokenService;
    }

    @GetMapping
    public List<MovieResponse> getAllPublishedMovies() {
        return movieService.getAllPublishedMovies();
    }

    @GetMapping("/admin")
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}/details")
    public MovieResponse getPublishedMovie(@PathVariable Long id) {
        return movieService.getPublishedMovie(id);
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> streamMovie(@PathVariable Long id) throws MalformedURLException {
        Resource resource = movieService.getMovieStream(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    @GetMapping("/{id}/stream-token")
    public ResponseEntity<String> getStreamToken(@PathVariable Long id) {
        String token = streamTokenService.generateToken(id);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}/poster")
    public ResponseEntity<Resource> getMoviePoster(@PathVariable Long id) throws MalformedURLException {
        Resource resource = movieService.getMoviePoster(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @PostMapping
    public MovieResponse createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        return movieService.createMovie(movieRequest);
    }

    @PostMapping("/{id}/video")
    public ResponseEntity<Void> uploadVideo(@PathVariable Long id, @RequestParam("video") MultipartFile video) {
        movieService.uploadVideo(id, video);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/poster")
    public ResponseEntity<Void> uploadPoster(@PathVariable Long id, @RequestParam("poster") MultipartFile poster) {
        movieService.uploadPoster(id, poster);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/publish")
    public MovieResponse publishMovie(@PathVariable Long id) {
        return movieService.publishMovie(id);
    }

    @PostMapping("/{id}/unpublish")
    public MovieResponse unpublishMovie(@PathVariable Long id) {
        return movieService.unpublishMovie(id);
    }

    @PutMapping("/{id}")
    public MovieResponse updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest movieRequest) {
        return movieService.updateMovie(id, movieRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
