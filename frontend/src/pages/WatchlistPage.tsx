import { useEffect, useState } from 'react';
import { getWatchlist } from "../api/watchlistApi";
import type { Movie } from "../types/Movie";
import type { MoviePage } from "../types/MoviePage";
import MovieCard from "../components/MovieCard";
import "./css/MovieListPage.css";
import Navbar from "../components/Navbar";

function WatchlistPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    
    async function loadMovies() {
      try {
        const moviePage: MoviePage = await getWatchlist(currentPage, 12, controller.signal);
        setMovies(moviePage.content);
        setTotalPages(moviePage.totalPages);
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
          <MovieCard
            key={movie.id}
            movie={movie}
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