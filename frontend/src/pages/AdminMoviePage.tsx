import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { deleteMovie, getMovies } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import AdminMovieCard from "../components/AdminMovieCard";
import Navbar from "../components/Navbar";

function AdminMoviePage() {
  const navigate = useNavigate();
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingMovieId, setDeletingMovieId] = useState<number | null>(null);
  
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

  async function handleDelete(movie: Movie) {
      const confirmed = window.confirm(`Are you sure you want to delete the movie: ${movie.title} (${movie.releaseYear})?`);
      if (!confirmed) {
          return;
      }

      try {
        setDeletingMovieId(movie.id);
        await deleteMovie(movie.id);
        const movieList = await getMovies();
        setMovies(movieList);
      } catch (error) {
        console.error(error);
        setError("Unable to delete movie.");
      } finally {
        setDeletingMovieId(null);
      }
  }

  if (loading) {
    return (
        <>
            <h1>Media Management</h1>
            <p>Loading movies...</p>
        </>
    );
  }

  if (error) {
    return (
      <>
        <h1>Media Management</h1>
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
            <h1>Media Management</h1>
            <button onClick={() => navigate("/admin/movies/new")}>
                Add New Movie
            </button>

            

            <div className="movie-grid">
                {movies.map(movie => (
                <AdminMovieCard
                    key={movie.id}
                    movie={movie}
                    onDelete={handleDelete}
                    deleting={deletingMovieId === movie.id}
                />
                ))}
            </div>
        </>
    );
}

export default AdminMoviePage;