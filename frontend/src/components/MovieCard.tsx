import type { Movie } from "../types/Movie";
import { Link } from "react-router-dom";
import "./MovieCard.css";

type MovieCardProps = {
    movie: Movie;
};

function MovieCard({ movie }: MovieCardProps) {
    return (
        <Link 
            className="movie-card"
            to={`/movies/${movie.id}`}
        >
            <img
                className="movie-poster"
                src={`http://localhost:8080/movies/${movie.id}/poster`}
                alt={`${movie.title} poster`}
            />
            <p>{movie.title} ({movie.releaseYear})</p>
        </Link>
    );
}

export default MovieCard;