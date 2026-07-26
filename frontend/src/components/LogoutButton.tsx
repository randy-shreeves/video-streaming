import { useNavigate } from "react-router-dom";

function LogoutButton() {
    const navigate = useNavigate();

    function handleLogout() {
        localStorage.removeItem("token");
        navigate("/", { replace: true });
    }

    return (
        <button onClick={handleLogout} className="logout-button">
            Logout
        </button>
    );
}

export default LogoutButton;