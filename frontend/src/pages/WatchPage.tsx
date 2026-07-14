import { useParams, useNavigate } from "react-router-dom";

function WatchPage() {
    const { id } = useParams();
    const videoUrl = `http://localhost:8080/movies/${id}/stream`;
    const navigate = useNavigate();
    return (
        <>
            <button onClick={() => navigate(`/movies/${id}`)}>
                Return to Movie Details
            </button>
            <video controls style={{
                width: "100%",
                maxWidth: "900px",
                aspectRatio: "16 / 9",
                objectFit: "contain"
            }}>
                <source src={videoUrl} type="video/mp4" />
            </video>
        </>
    );
}

export default WatchPage;