import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import type { MovieRequest } from "../types/MovieRequest";
import "./css/AddMoviePage.css";
import { createMovie } from "../api/movieApi";
import type { SyntheticEvent } from "react";
import Navbar from "../components/Navbar";

function AddMoviePage() {
    const navigate = useNavigate();
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

    const handleSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            const movie = await createMovie(formData);
            console.log("Created movie:", movie);
            navigate("/admin/movies");
        } catch (error) {
            console.error(error);
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError("Unable to create movie.");
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <>
            {error && <p>{error}</p>}

            <Navbar
                backPath="/admin/movies"
                backLabel="Media Management"
            />

            <h1>Add Movie</h1>

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
                    {submitting ? "Creating..." : "Add Movie"}
                </button>
            </form>
        </>
    );
}

export default AddMoviePage;