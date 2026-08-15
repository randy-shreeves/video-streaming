import { useEffect, useState } from 'react';
import { getMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import MovieCard from "../components/MovieCard";
import "./css/MovieListPage.css";
import Navbar from "../components/Navbar";

function MovieListPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  useEffect(() => {
    const controller = new AbortController();
    
    async function loadMovies() {
      try {
        const movieList = await getMovies(controller.signal);
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
  }, []);

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