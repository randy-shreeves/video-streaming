import { useEffect, useState } from 'react';
import { getWatchlist } from "../api/watchlistApi";
import type { Movie } from "../types/Movie";
import type { MoviePage } from "../types/MoviePage";
import WatchlistMovieCard from "../components/WatchlistMovieCard";
import "./css/MovieListPage.css";
import Navbar from "../components/Navbar";
import { removeFromWatchlist } from '../api/watchlistApi';

function WatchlistPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [removingMovieId, setRemovingMovieId] = useState<number | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    
    async function loadMovies() {
      try {
        const moviePage: MoviePage = await getWatchlist(currentPage, 12, controller.signal);
        setMovies(moviePage.content);
        if (moviePage.totalPages === 0) {
          setTotalPages(1);
        } else {
          setTotalPages(moviePage.totalPages);
        }
      } catch (error) {
        if (error instanceof DOMException && error.name === "AbortError") {
            return;
        }
        console.error(error);
        setError("Unable to load watchlist.");
      }
      finally {
        setLoading(false);
      }
    }

    loadMovies();

    return () => {
      controller.abort();
    };
  }, [currentPage]);

  async function handleRemoval(movie: Movie) {
    try {
      setRemovingMovieId(movie.id);
      setError(null);
      await removeFromWatchlist(movie.id);
      const moviePage: MoviePage = await getWatchlist(currentPage, 12);
      setMovies(moviePage.content);
      if (moviePage.totalPages === 0) {
        setTotalPages(1);
      } else {
        setTotalPages(moviePage.totalPages);
      }
    } catch (error) {
      console.error(error);
      setError("Unable to remove movie.");
    } finally {
      setRemovingMovieId(null);
    }
  }

  if (loading) {
    return (
        <>
            <h1>Watchlist</h1>
            <p>Loading movies...</p>
        </>
    );
  }

  if (error) {
    return (
      <>
        <h1>Watchlist</h1>
        <p>{error}</p>
      </>

    );
  }

  return (
    <>
      <Navbar
        backPath="/movies"
        backLabel="Movie Catalog" 
      />

      <h1>Watchlist</h1>

      <div className="movie-grid">
        {movies.map(movie => (
          <WatchlistMovieCard
            key={movie.id}
            movie={movie}
            onRemoval={handleRemoval}
            removing={removingMovieId === movie.id}
          />
        ))}
      </div>

      <div className="pagination">
        <button
            onClick={() => setCurrentPage(currentPage - 1)}
            disabled={currentPage === 0}
        >
            Previous
        </button>

        <span>
          Page {currentPage + 1} of {totalPages}
        </span>

        <button
            onClick={() => setCurrentPage(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
        >
            Next
        </button>
      </div>
    </>
  );
}

export default WatchlistPage;