import "./LoginPage.css";
import { login } from "../api/authApi";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SyntheticEvent } from "react";

function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const data = await login(username, password);
            localStorage.setItem("token", data.token);
            navigate("/movies");

        } catch (error) {
            if (error instanceof Error) {
                setErrorMessage(error.message);
            }
        }
    };

    return (
        <div className="login-container">
            <h1>Video Streaming</h1>

            {errorMessage && <p className="error">{errorMessage}</p>}

            <form onSubmit={handleLogin}>
                <div>
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                </div>

                <div className="button-row">
                    <button type="submit">Log In</button>
                    <button 
                        type="button" 
                        onClick={() => navigate("/register")}
                    >
                        Register
                    </button>
                </div>
            </form>
        </div>
    );
}

export default LoginPage;