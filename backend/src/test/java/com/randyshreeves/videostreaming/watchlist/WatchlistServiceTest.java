package com.randyshreeves.videostreaming.watchlist;

import com.randyshreeves.videostreaming.movie.Movie;
import com.randyshreeves.videostreaming.movie.MovieRepository;
import com.randyshreeves.videostreaming.movie.dto.MovieResponse;
import com.randyshreeves.videostreaming.user.Role;
import com.randyshreeves.videostreaming.user.User;
import com.randyshreeves.videostreaming.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class WatchlistServiceTest {

    @MockitoBean
    WatchlistRepository watchlistRepository;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    MovieRepository movieRepository;

    @Autowired
    WatchlistService watchlistService;

    @Test
    void shouldReturnPublishedMoviesInWatchlist() {
        Long userId = 1L;
        Movie movie1 = createTestMovie();
        Movie movie2 = createTestMovie();
        Watchlist watchlist1 = new Watchlist();
        watchlist1.setMovie(movie1);
        Watchlist watchlist2 = new Watchlist();
        watchlist2.setMovie(movie2);
        Pageable pageable = PageRequest.of(0, 12);
        Page<Watchlist> watchlistPage = new PageImpl<>(
                List.of(watchlist1, watchlist2),
                pageable,
                2
        );
        when(watchlistRepository
                .findByUserIdAndMoviePublishedTrueOrderByIdAsc(userId, pageable))
                .thenReturn(watchlistPage);
        Page<MovieResponse> result = watchlistService.getWatchlist(userId, 0, 12);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(movie1.getId(), result.getContent().get(0).getId());
        assertEquals(movie2.getId(), result.getContent().get(1).getId());
        verify(watchlistRepository).findByUserIdAndMoviePublishedTrueOrderByIdAsc(userId, pageable);
    }

    @Test
    void shouldAddMovieToWatchlist() {
        Long userId = 1L;
        Long movieId = 1L;
        User user = createTestUser();
        Movie movie = createTestMovie();
        movie.setPublished(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(watchlistRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        watchlistService.addToWatchlist(userId, movieId);
        ArgumentCaptor<Watchlist> watchlistCaptor = ArgumentCaptor.forClass(Watchlist.class);
        verify(watchlistRepository).save(watchlistCaptor.capture());
        Watchlist savedWatchlist = watchlistCaptor.getValue();
        assertEquals(user, savedWatchlist.getUser());
        assertEquals(movie, savedWatchlist.getMovie());
    }

    @Test
    void shouldRemoveMovieFromWatchlist() {
        Long userId = 1L;
        Long movieId = 1L;
        Watchlist watchlist = new Watchlist();
        when(watchlistRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(watchlist));
        watchlistService.removeFromWatchlist(userId, movieId);
        verify(watchlistRepository).delete(watchlist);
    }

    private Movie createTestMovie() {
        return new Movie(
                "Test Movie",
                "Test Movie Description",
                2009,
                90
        );
    }

    private User createTestUser() {
        return new User(
                "TestUser",
                "TestPassword",
                Role.ROLE_USER
        );
    }
}
