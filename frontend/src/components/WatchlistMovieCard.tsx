import type { Movie } from "../types/Movie";
import "./css/MovieCard.css";
import { getMoviePoster } from "../api/movieApi";
import { useEffect, useState } from "react";

type MovieCardProps = {
    movie: Movie;
    onRemoval: (movie: Movie) => void;
    removing: boolean;
};

function WatchlistMovieCard({ movie, onRemoval, removing }: MovieCardProps) {
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
        <div className="movie-card">

            <img
                className="movie-poster"
                src={posterUrl ?? undefined}
                alt={`${movie.title} poster`}
            />

            <p>{movie.title} ({movie.releaseYear})</p>

            <button 
                onClick={() => onRemoval(movie)}
                disabled={removing}
            >
                    {removing ? "Removing..." : "Remove"}
            </button>

        </div>

    );
}

export default WatchlistMovieCard;