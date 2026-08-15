import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getMovieStream } from "../api/movieApi";
import Navbar from "../components/Navbar";

function WatchPage() {
    const [videoUrl, setVideoUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const { id } = useParams();
    const movieId = Number(id);

    if (Number.isNaN(movieId)) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        let objectUrl: string | null = null;
        const controller = new AbortController();
        let aborted = false;
        
        async function loadVideo() {
            try {
                objectUrl = await getMovieStream(movieId, controller.signal);
                setVideoUrl(objectUrl);
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
            } finally {
                if (!aborted) {
                    setLoading(false);
                }
            }
        }

        loadVideo();

        return () => {
            controller.abort();
            if (objectUrl) {
                URL.revokeObjectURL(objectUrl);
            }
        };
    }, [movieId]);

    if (loading) {
        return <p>Loading movie...</p>;
    }

    if (error) {
        return <p>{error}</p>; 
    }

    return (
        <>
            <Navbar
                backPath={`/movies/${movieId}`}
                backLabel="Movie Details"
            />

            {videoUrl && (
                <video controls style={{
                    width: "100%",
                    maxWidth: "900px",
                    aspectRatio: "16 / 9",
                    objectFit: "contain"
                }}>
                    <source src={videoUrl} type="video/mp4" />
                </video>
            )}
        </>
    );
}

export default WatchPage;