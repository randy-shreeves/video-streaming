package com.randyshreeves.videostreaming.watchlist;

import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import com.randyshreeves.videostreaming.user.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public Page<MovieResponse> getWatchlist(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return watchlistService.getWatchlist(user.getId(), page, size);
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<Void> addToWatchlist(
            @AuthenticationPrincipal User user,
            @PathVariable Long movieId
    ) {
        watchlistService.addToWatchlist(user.getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFromWatchlist(
            @AuthenticationPrincipal User user,
            @PathVariable Long movieId
    ) {
        watchlistService.removeFromWatchlist(user.getId(), movieId);
        return ResponseEntity.noContent().build();
    }
}
