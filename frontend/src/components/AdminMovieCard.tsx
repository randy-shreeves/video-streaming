import type { Movie } from "../types/Movie";
import "./css/MovieCard.css";
import { getPublishedMoviePoster } from "../api/movieApi";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

type MovieCardProps = {
    movie: Movie;
    onDelete: (movie: Movie) => void;
    deleting: boolean;
    onPublicationChange: (movie: Movie) => void;
    updatingPublicationStatus: boolean;
};

function AdminMovieCard({ movie, onDelete, deleting, onPublicationChange, updatingPublicationStatus}: MovieCardProps) {
    const [posterUrl, setPosterUrl] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        let objectUrl: string | null = null;
        const controller = new AbortController();

        async function loadMoviePoster() {
            try {
                objectUrl = await getPublishedMoviePoster(movie.id, controller.signal);
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
            <button
                onClick={() => onPublicationChange(movie)}
                disabled={updatingPublicationStatus}
            >
                {updatingPublicationStatus
                    ? movie.published
                        ? "Unpublishing..."
                        : "Publishing..."
                    : movie.published
                        ? "Unpublish"
                        : "Publish"}
            </button>
        </div>

    );
}

export default AdminMovieCard;