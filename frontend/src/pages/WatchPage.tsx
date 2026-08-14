import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { getMovieStream } from "../api/movieApi";
import LogoutButton from "../components/LogoutButton";

function WatchPage() {
    const [videoUrl, setVideoUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const { id } = useParams();
    const navigate = useNavigate();
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
            <div className="page-navigation">
                <button onClick={() => navigate(`/movies/${id}`)}>
                    Return to Movie Details
                </button>
                <LogoutButton />
            </div>
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