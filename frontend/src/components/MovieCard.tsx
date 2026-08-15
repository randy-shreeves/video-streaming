import type { Movie } from "../types/Movie";
import { Link } from "react-router-dom";
import "./css/MovieCard.css";
import { getMoviePoster } from "../api/movieApi";
import { useEffect, useState } from "react";

type MovieCardProps = {
    movie: Movie;
};

function MovieCard({ movie }: MovieCardProps) {
    const [posterUrl, setPosterUrl] = useState<string | null>(null);

    useEffect(() => {
        let objectUrl: string | null = null;
        const controller = new AbortController();

        async function loadMoviePoster() {
            try {
                objectUrl = await getMoviePoster(movie.id, controller.signal);
                setPosterUrl(objectUrl);
            } catch (error) {
                if (error instanceof DOMException && error.name === "AbortError") {
                    return;
                }
                console.error(error);
            }
        }

        loadMoviePoster();

        return () => {
            controller.abort();
            if(objectUrl) {
                URL.revokeObjectURL(objectUrl);
            }
        };
    }, [movie.id]);

    return (
        <Link 
            className="movie-card"
            to={`/movies/${movie.id}`}
        >
            <img
                className="movie-poster"
                src={posterUrl ?? undefined}
                alt={`${movie.title} poster`}
            />
            <p>{movie.title} ({movie.releaseYear})</p>
        </Link>
    );
}

export default MovieCard;