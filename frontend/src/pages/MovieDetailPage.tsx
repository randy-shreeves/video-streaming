import { Link, useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react';
import { getMovie } from "../api/movieApi";
import type { Movie } from "../types/Movie";


function MovieDetailPage() {
    const [movie, setMovie] = useState<Movie | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { id } = useParams();
    const navigate = useNavigate();

    if (!id) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        async function loadMovie() {
            try {
                const movie: Movie = await getMovie(Number(id));
                setMovie(movie);
            } catch (error) {
                console.error(error);
                setError("Unable to load movie.");
            }
            finally {
                setLoading(false);
            }
        }
        if(id) {
            loadMovie();
        }
    }, [id]);

    if (loading) {
        return <p>Loading movie...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    if (!movie) {
        return <p>Movie not found.</p>;
    }

    return (
        <>
            <button onClick={() => navigate("/movies")}>
                Return to Movie Catalog
            </button>
            <div className="poster-container">
                <Link to={`/movies/${movie.id}/watch`}>
                    <img 
                        className="movie-poster"
                        src={`http://localhost:8080/movies/${movie.id}/poster`} 
                        alt={`${movie.title} poster`}
                    />
                    <div className="play-overlay">
                        ▶
                    </div>
                </Link>
            </div>
            <h1>{movie.title} ({movie.releaseYear})</h1>
            <p>{movie.description}</p>
            <p>
                {Math.floor(movie.runtimeMinutes / 60)}h{" "}
                {movie.runtimeMinutes % 60}m
            </p>
        </>
    );
}

export default MovieDetailPage;