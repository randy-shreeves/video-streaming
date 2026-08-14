import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import LogoutButton from "../components/LogoutButton";
import MovieCard from "../components/MovieCard";

function AdminMoviePage() {
  const navigate = useNavigate();
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
            <h1>Movie Management</h1>
            
            <button onClick={() => navigate("/admin/movies/new")}>
                Add Movie
            </button>

            <LogoutButton />

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

export default AdminMoviePage;