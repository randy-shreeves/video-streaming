import { useEffect, useState } from 'react';
import { getPublishedMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import type { MoviePage } from "../types/MoviePage";
import MovieCard from "../components/MovieCard";
import "./css/MovieListPage.css";
import Navbar from "../components/Navbar";

function MovieListPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [activeSearch, setActiveSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const handleSearch = async (event: React.SyntheticEvent<HTMLFormElement>) => {
      event.preventDefault();
      setCurrentPage(0);
      setActiveSearch(searchInput);
    }
  
  const clearSearch = () => {
    setSearchInput("");
    setActiveSearch("");
    setCurrentPage(0);
  }

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    
    async function loadMovies() {
      try {
        const moviePage: MoviePage = await getPublishedMovies(activeSearch, currentPage, 12, controller.signal);
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
        setError("Unable to load the movie catalog.");
      }
      finally {
        setLoading(false);
      }
    }

    loadMovies();

    return () => {
      controller.abort();
    };
  }, [activeSearch, currentPage]);

  if (loading) {
    return (
        <>
            <h1>Movie Catalog</h1>
            <p>Loading movies...</p>
        </>
    );
  }

  if (error) {
    return (
      <>
        <h1>Movie Catalog</h1>
        <p>{error}</p>
      </>

    );
  }

  return (
    <>
      <Navbar />
      <h1>Movie Catalog</h1>

      <form className="movie-search" onSubmit={handleSearch}>
        <input
          type="search"
          placeholder="Search movies by title"
          value={searchInput}
          onChange={(event) => setSearchInput(event.target.value)}
        />
        <button type="submit">Search</button>
        <button type="button" onClick={clearSearch}>Clear</button>
      </form>

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

export default MovieListPage;