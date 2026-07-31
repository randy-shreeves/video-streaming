import { Link, useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react';
import { getMovie, getMoviePoster } from "../api/movieApi";
import type { Movie } from "../types/Movie";
import LogoutButton from "../components/LogoutButton";


function MovieDetailPage() {
    const [movie, setMovie] = useState<Movie | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [posterUrl, setPosterUrl] = useState<string | null>(null);
    const { id } = useParams();
    const navigate = useNavigate();

    if (!id) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        let objectUrl: string | null = null;
        const controller = new AbortController();

        async function loadMovie() {
            try {
                const movie: Movie = await getMovie(Number(id), controller.signal);
                objectUrl = await getMoviePoster(movie.id);
                setMovie(movie);
                setPosterUrl(objectUrl);
            } catch (error) {
                if (error instanceof DOMException && error.name === "AbortError") {
                    return;
                }
                console.error(error);
                setError("Unable to load movie.");
            }
            finally {
                setLoading(false);
            }
        }

        loadMovie();

        return () => {
            controller.abort();
            if(objectUrl) {
                URL.revokeObjectURL(objectUrl);
            }
        };
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
            <LogoutButton />
            <button onClick={() => navigate("/movies")}>
                Return to Movie Catalog
            </button>
            <div className="poster-container">
                <Link to={`/movies/${movie.id}/watch`}>
                    <img 
                        className="movie-poster"
                        src={posterUrl ?? undefined} 
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