import { useParams } from "react-router-dom";

function WatchPage() {
    const { id } = useParams();
    const videoUrl = `http://localhost:8080/movies/${id}/stream`;
    return (
        <>
            <video controls style={{
                width: "100%",
                maxWidth: "900px",
                height: "500px",
                objectFit: "contain"
            }}>
                <source src={videoUrl} type="video/mp4" />
            </video>
        </>
    );
}

export default WatchPage;