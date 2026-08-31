package com.randyshreeves.videostreaming.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserIdOrderByIdAsc(Long userId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId);
}