import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { deleteMovie, getAllMovies, publishMovie, unpublishMovie } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import AdminMovieCard from "../components/AdminMovieCard";
import Navbar from "../components/Navbar";

function AdminMoviePage() {
  const navigate = useNavigate();
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingMovieId, setDeletingMovieId] = useState<number | null>(null);
  const [updatingPublicationStatusForMovieId, setUpdatingPublicationStatusForMovieId] = useState<number | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [activeSearch, setActiveSearch] = useState("");
  
  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    
    async function loadMovies() {
      try {
        const movieList = await getAllMovies(activeSearch, controller.signal);
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

  async function handleDelete(movie: Movie) {
      const confirmed = window.confirm(`Are you sure you want to delete the movie: ${movie.title} (${movie.releaseYear})?`);
      if (!confirmed) {
          return;
      }

      try {
        setDeletingMovieId(movie.id);
        await deleteMovie(movie.id);
        const movieList = await getAllMovies();
        setMovies(movieList);
      } catch (error) {
        console.error(error);
        setError("Unable to delete movie.");
      } finally {
        setDeletingMovieId(null);
      }
  }

  async function handlePublicationChange(movie: Movie) {
    const confirmed = window.confirm(`Are you sure you want to change publication status of this movie: ${movie.title} (${movie.releaseYear})?`);
    if (!confirmed) {
      return;
    }

    try {
      setUpdatingPublicationStatusForMovieId(movie.id);
      if (movie.published) {
        await unpublishMovie(movie.id);
      } else {
        await publishMovie(movie.id);
      }
      const movieList = await getAllMovies();
      setMovies(movieList);
    } catch (error) {
      setError("Unable to change movie publication status.");
    } finally {
      setUpdatingPublicationStatusForMovieId(null);
    }
  }

  const handleSearch = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setActiveSearch(searchInput);
    }
  
  const clearSearch = () => {
    setSearchInput("");
    setActiveSearch("");
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
            
            <button onClick={() => navigate("/admin/movies/new")}>
                Add New Movie
            </button>

            <div className="movie-grid">
                {movies.map(movie => (
                <AdminMovieCard
                    key={movie.id}
                    movie={movie}
                    onDelete={handleDelete}
                    onPublicationChange={handlePublicationChange}
                    deleting={deletingMovieId === movie.id}
                    updatingPublicationStatus={updatingPublicationStatusForMovieId === movie.id}
                />
                ))}
            </div>
        </>
    );
}

export default AdminMoviePage;