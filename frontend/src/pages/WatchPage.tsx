import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getStreamToken } from "../api/movieApi";
import Navbar from "../components/Navbar";

const BASE_URL = "http://localhost:8080";

function WatchPage() {
    const [streamUrl, setStreamUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const { id } = useParams();
    const movieId = Number(id);

    if (Number.isNaN(movieId)) {
        return <p>Movie not found.</p>;
    }

    useEffect(() => {
        const controller = new AbortController();
        let aborted = false;
        
        async function loadStreamUrl() {
            try {
                const token = await getStreamToken(movieId, controller.signal);
                const url = `${BASE_URL}/movies/${movieId}/stream?token=${encodeURIComponent(token)}`;
                setStreamUrl(url);
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

        loadStreamUrl();

        return () => {
            controller.abort();
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

            <video 
                controls 
                style={{
                    width: "100%",
                    maxWidth: "900px",
                    aspectRatio: "16 / 9",
                    objectFit: "contain"
                }}
            >
                <source 
                    src={streamUrl ?? undefined} 
                    type="video/mp4" 
                />
            </video>
        </>
    );
}

export default WatchPage;