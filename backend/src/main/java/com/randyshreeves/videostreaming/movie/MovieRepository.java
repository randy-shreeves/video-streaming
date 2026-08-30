package com.randyshreeves.videostreaming.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByPublishedTrue(Pageable pageable);
    Page<Movie> findByPublishedTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
