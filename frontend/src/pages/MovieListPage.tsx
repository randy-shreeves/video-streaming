import { useEffect, useState } from 'react';
import { getPublishedMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import MovieCard from "../components/MovieCard";
import "./css/MovieListPage.css";
import Navbar from "../components/Navbar";

function MovieListPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [activeSearch, setActiveSearch] = useState("");

  const handleSearch = async (event: React.SyntheticEvent<HTMLFormElement>) => {
      event.preventDefault();
      setActiveSearch(searchInput);
    }
  
  const clearSearch = () => {
    setSearchInput("");
    setActiveSearch("");
  }

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    
    async function loadMovies() {
      try {
        const movieList = await getPublishedMovies(activeSearch, controller.signal);
        setMovies(movieList);
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
  }, [activeSearch]);

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
    </>
  );
}

export default MovieListPage;