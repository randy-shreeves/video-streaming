package com.randyshreeves.videostreaming.watchlist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    Page<Watchlist> findByUserIdAndMoviePublishedTrueOrderByIdAsc(Long userId, Pageable pageable);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId);
}