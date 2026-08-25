import { useEffect, useRef, useState } from 'react';
import { useNavigate } from "react-router-dom";
import type { MovieRequest } from "../types/MovieRequest";
import "./css/AddMoviePage.css";
import { createMovie, uploadPoster, uploadVideo } from "../api/movieApi";
import type { SyntheticEvent } from "react";
import Navbar from "../components/Navbar";

function AddMoviePage() {
    const navigate = useNavigate();
    const posterInputRef = useRef<HTMLInputElement>(null);
    const [poster, setPoster] = useState<File | null>(null);
    const [posterPreview, setPosterPreview] = useState<string | null>(null);
    const videoInputRef = useRef<HTMLInputElement>(null);
    const [video, setVideo] = useState<File | null>(null);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [formData, setFormData] = useState<MovieRequest>({
        title: "",
        description: "",
        releaseYear: 0,
        runtimeMinutes: 0
    });

    useEffect(() => {
        let objectUrl: string | null = null;
            if (!poster) {
                setPosterPreview(null);
                return;
            }
            objectUrl = URL.createObjectURL(poster);
            setPosterPreview(objectUrl);
    
            return () => {
                URL.revokeObjectURL(objectUrl);
            };             
        }, [poster]);

    const handleSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            const movie = await createMovie(formData);
            if (poster) {
                await uploadPoster(movie.id, poster);
            }
            if (video) {
                await uploadVideo(movie.id, video);
            }
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
                    <label>Video</label>
                    <input
                        ref={videoInputRef}
                        type="file"
                        accept="video/mp4"
                        onChange={(event) => {
                            setVideo(event.target.files?.[0] ?? null);
                        }}
                    />
                    {video && (
                        <div className="video-selection">
                            {video.name}
                            <button
                                type="button"
                                onClick={() => {
                                    setVideo(null);
                                    if (videoInputRef.current) {
                                        videoInputRef.current.value = "";
                                    }
                                }}
                            >
                                X
                            </button>
                        </div>
                    )}
                </div>

                <div className="form-field">
                    <label>Poster</label>
                    <input
                        ref={posterInputRef}
                        type="file"
                        accept="image/jpeg"
                        onChange={(event) => {
                            setPoster(event.target.files?.[0] ?? null);
                        }}
                    />

                    {posterPreview && (
                        <div className="poster-preview">
                            <img
                                src={posterPreview}
                                alt="Selected poster preview"
                            />
                            <button
                                type="button"
                                onClick={() => {
                                    setPoster(null);
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
                    {submitting ? "Creating..." : "Add Movie"}
                </button>
            </form>
        </>
    );
}

export default AddMoviePage;