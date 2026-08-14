import { useNavigate } from "react-router-dom";

function AdminMoviePage() {
    const navigate = useNavigate();

    return (
        <>
            <h1>Movie Management</h1>
            
            <button onClick={() => navigate("/admin/movies/new")}>
                Add Movie
            </button>
        </>
    );
}

export default AdminMoviePage;