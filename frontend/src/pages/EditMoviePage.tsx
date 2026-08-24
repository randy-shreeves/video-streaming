import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState, useRef, type SyntheticEvent } from 'react';
import type { Movie} from "../types/Movie";
import { updateMovie, getMovie, getMoviePoster, uploadPoster } from "../api/movieApi";
import type { MovieRequest } from "../types/MovieRequest";
import Navbar from "../components/Navbar";
import "./css/EditMoviePage.css";

function EditMoviePage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const movieId = Number(id);
    const posterInputRef = useRef<HTMLInputElement>(null);
    const [currentPosterUrl, setCurrentPosterUrl] = useState<string | null>(null);
    const [newPoster, setNewPoster] = useState<File | null>(null);
    const [newPosterPreview, setNewPosterPreview] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [formData, setFormData] = useState<MovieRequest>({
            title: "",
            description: "",
            releaseYear: 0,
            runtimeMinutes: 0
        });

    if (Number.isNaN(movieId)) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        const controller = new AbortController();
        let aborted = false;
        let objectUrl: string | null = null;

        async function loadMovie() {
            try {
                const movie: Movie = await getMovie(movieId, controller.signal);
                objectUrl = await getMoviePoster(movie.id);
                setCurrentPosterUrl(objectUrl);
                setFormData({
                    title: movie.title,
                    description: movie.description,
                    releaseYear: movie.releaseYear,
                    runtimeMinutes: movie.runtimeMinutes
                });
            } catch (error) {
                if (error instanceof DOMException && error.name === "AbortError") {
                    aborted = true;
                    return;
                } else if(error instanceof Error) {
                    setError(error.message);
                } else {
                    setError("Unable to load movie.");
                }
                console.error(error);
            }
            finally {
                if (!aborted) {
                    setLoading(false);
                }
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

    useEffect(() => {
    let objectUrl: string | null = null;
        if (!newPoster) {
            setNewPosterPreview(null);
            return;
        }
        objectUrl = URL.createObjectURL(newPoster);
        setNewPosterPreview(objectUrl);

        return () => {
            URL.revokeObjectURL(objectUrl);
        };             
    }, [newPoster]);

    const handleSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);

        try {
            const movie = await updateMovie(movieId, formData);
            if (newPoster) {
                await uploadPoster(movie.id, newPoster);
            }
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

            {loading && <p>Loading...</p>}

            <Navbar
                backPath="/admin/movies"
                backLabel="Media Management"
            />

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
                    <label>Current Poster</label>
                    <img
                        src={currentPosterUrl ?? undefined}
                        alt={`${formData.title} poster`}
                    />
                </div>

                <div className="form-field">
                    <label>Replace Poster</label>
                    <input
                        ref={posterInputRef}
                        type="file"
                        accept="image/jpeg"
                        onChange={(event) => {
                            setNewPoster(event.target.files?.[0] ?? null);
                        }}
                    />
                    
                    {newPosterPreview && (
                        <div className="poster-preview">
                            <img
                                src={newPosterPreview}
                                alt="Selected poster preview"
                            />
                            <button
                                type="button"
                                onClick={() => {
                                    setNewPoster(null);
                                    if (posterInputRef.current) {
                                        posterInputRef.current.value = "";
                                    }
                                }}
                            >
                                X
                            </button>
                        </div>
                    )}

                </div>

                <button type="submit" disabled={submitting}>
                    {submitting ? "Updating..." : "Edit Movie"}
                </button>
            </form>
        </>
    )
}

export default EditMoviePage;