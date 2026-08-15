import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState, type SyntheticEvent } from 'react';
import type { Movie} from "../types/Movie";
import { updateMovie, getMovie } from "../api/movieApi";
import LogoutButton from "../components/LogoutButton";
import type { MovieRequest } from "../types/MovieRequest";

function getMovieFileName(title: string): string {
    return title
        .toLowerCase()
        .trim()
        .replace(/\s+/g, "_");
}

function EditMoviePage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const movieId = Number(id);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [formData, setFormData] = useState<MovieRequest>({
            title: "",
            description: "",
            releaseYear: 0,
            runtimeMinutes: 0,
            storageLocation: "",
            posterLocation: ""
        });

    if (Number.isNaN(movieId)) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        const controller = new AbortController();

        async function loadMovie() {
            try {
                const movie: Movie = await getMovie(movieId, controller.signal);
                const fileName = getMovieFileName(movie.title);
                setFormData({
                    title: movie.title,
                    description: movie.description,
                    releaseYear: movie.releaseYear,
                    runtimeMinutes: movie.runtimeMinutes,
                    storageLocation: `movies/${fileName}.mp4`,
                    posterLocation: `movies/posters/${fileName}.jpg`
                });
            } catch (error) {
                if (error instanceof DOMException && error.name === "AbortError") {
                    return;
                }
                console.error(error);
            }
        }

        loadMovie();

        return () => {
            controller.abort();
        };
    }, [id]);

    const handleSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);

        try {
            const movie = await updateMovie(movieId, formData);
            console.log("Edited movie:", movie);
            navigate("/admin/movies");
        } catch (error) {
            console.error(error);
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError("Unable to edit movie.");
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <>
            {error && <p>{error}</p>}

            <div className="page-navigation">
                <button onClick={() => navigate("/admin/movies")}>
                    Return to Admin Movie Catalog
                </button>
                <LogoutButton />
            </div>

            <h1>Edit Movie</h1>

            <form className="movie-form" onSubmit={handleSubmit}>
                <div className="form-field">
                    <label>Title</label>
                    <input 
                        type="text"
                        value={formData.title}
                        onChange={(event) => setFormData({
                                ...formData,
                                title: event.target.value
                            })
                        }
                    />
                </div>

                <div className="form-field">
                    <label>Description</label>
                    <textarea
                        value={formData.description}
                        onChange={(event) =>
                            setFormData({
                                ...formData,
                                description: event.target.value
                            })
                        }
                    />
                </div>

                <div className="form-field">
                    <label>Release Year</label>
                    <input
                        type="number"
                        value={formData.releaseYear}
                        onChange={(event) =>
                            setFormData({
                                ...formData,
                                releaseYear: Number(event.target.value)
                            })
                        }
                    />
                </div>

                <div className="form-field">
                    <label>Runtime (minutes)</label>
                    <input
                        type="number"
                        value={formData.runtimeMinutes}
                        onChange={(event) =>
                            setFormData({
                                ...formData,
                                runtimeMinutes: Number(event.target.value)
                            })
                        }
                    />
                </div>

                <div className="form-field">
                    <label>Storage Location</label>
                    <input
                        type="text"
                        value={formData.storageLocation}
                        onChange={(event) =>
                            setFormData({
                                ...formData,
                                storageLocation: event.target.value
                            })
                        }
                    />
                </div>

                <div className="form-field">
                    <label>Poster Location</label>
                    <input
                        type="text"
                        value={formData.posterLocation}
                        onChange={(event) =>
                            setFormData({
                                ...formData,
                                posterLocation: event.target.value
                            })
                        }
                    />
                </div>

                <button type="submit" disabled={submitting}>
                    {submitting ? "Updating..." : "Edit Movie"}
                </button>
            </form>
        </>
    )
}

export default EditMoviePage;