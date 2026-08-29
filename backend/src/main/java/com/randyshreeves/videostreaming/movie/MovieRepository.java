package com.randyshreeves.videostreaming.movie;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAllByOrderByIdAsc();
    List<Movie> findByPublishedTrueOrderByIdAsc();
    List<Movie> findByPublishedTrueAndTitleContainingIgnoreCaseOrderByIdAsc(String title);
}
