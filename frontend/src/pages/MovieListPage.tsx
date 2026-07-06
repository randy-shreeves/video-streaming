import { useEffect, useState } from 'react';
import { Link } from "react-router-dom";
import { getMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";

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

      {movies.map(movie => (
        <p key={movie.id}>
          <Link to={`/movies/${movie.id}`}>
            {movie.title}
          </Link>
        </p>
      ))}
    </>
  );
}

export default MovieListPage;