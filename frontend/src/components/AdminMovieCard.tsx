import type { Movie } from "../types/Movie";
import "./MovieCard.css";
import { getMoviePoster } from "../api/movieApi";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

type MovieCardProps = {
    movie: Movie;
    onDelete: (movie: Movie) => void;
    deleting: boolean;
};

function AdminMovieCard({ movie, onDelete, deleting }: MovieCardProps) {
    const [posterUrl, setPosterUrl] = useState<string | null>(null);
    const navigate = useNavigate();

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
            <button onClick={() => navigate(`/admin/movies/${movie.id}/edit`)}>Edit</button>
            <button 
                onClick={() => onDelete(movie)}
                disabled={deleting}
            >
                    {deleting ? "Deleting..." : "Delete"}
            </button>
        </div>

    );
}

export default AdminMovieCard;