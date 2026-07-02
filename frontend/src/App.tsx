import { useEffect, useState } from 'react';
import { getMovies } from "./api/movieApi";
import type { Movie } from "./types/Movie";

function App() {
  const [movies, setMovies] = useState<Movie[]>([]);
  useEffect(() => {
    async function loadMovies() {
      try {
        const movieList = await getMovies();
        setMovies(movieList);
      } catch (error) {
        console.error(error);
      }
    }
    loadMovies();
  }, []);

  return (
    <>
      <h1>Video Streaming</h1>

      {movies.map(movie => (
        <p key={movie.id}>
          {movie.title}
        </p>
      ))}
    </>
  );
}

export default App;