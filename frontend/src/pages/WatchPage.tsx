import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { getMovieStream } from "../api/movieApi";
import LogoutButton from "../components/LogoutButton";

function WatchPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [videoUrl, setVideoUrl] = useState<string | null>(null);

    if (!id) {
        return <p>Movie not found.</p>;
    }
    
    useEffect(() => {
        getMovieStream(Number(id))
            .then(url => {
                setVideoUrl(url);
            })
            .catch(error => {
                console.error(error);
            });
    }, [id]);

    return (
        <>
            <LogoutButton />
            <button onClick={() => navigate(`/movies/${id}`)}>
                Return to Movie Details
            </button>
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