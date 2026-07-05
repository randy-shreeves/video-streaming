import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react';
import { getMovie } from "../api/movieApi";
import type { Movie } from "../types/Movie";


function MovieDetailPage() {
    const [movie, setMovie] = useState<Movie | null>(null);
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
            }
        }
        if(id) {
            loadMovie();
        }
    }, [id]);

    if (!movie) {
        return <p>Loading...</p>;
    }

    return (
        <>
            <h1>Video Streaming</h1>
            <h2>{movie.title}</h2>
            <p>({movie.releaseYear})</p>
            <p>{movie.description}</p>
            <p>Runtime: {movie.runtimeMinutes} minutes</p>
        </>
    );
}

export default MovieDetailPage;