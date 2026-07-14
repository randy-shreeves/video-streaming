import { useEffect, useState } from 'react';
import { getMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import MovieCard from "../components/MovieCard";
import "./MovieListPage.css";

function MovieListPage() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    async function loadMovies() {
      try {
        const movieList = await getMovies();
        setMovies(movieList);
      } catch (error) {
        console.error(error);
        setError("Unable to load the movie catalog.");
      }
      finally {
        setLoading(false);
      }
    }
    loadMovies();
  }, []);

  if (loading) {
    return (
        <>
            <h1>Video Streaming</h1>
            <p>Loading movies...</p>
        </>
    );
  }

  if (error) {
    return (
      <>
        <h1>Video Streaming</h1>
        <p>{error}</p>
      </>

    );
  }

  return (
    <>
      <h1>Video Streaming</h1>

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