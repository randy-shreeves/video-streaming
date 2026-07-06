import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react';
import { getMovie } from "../api/movieApi";
import type { Movie } from "../types/Movie";


function MovieDetailPage() {
    const [movie, setMovie] = useState<Movie | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { id } = useParams();

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