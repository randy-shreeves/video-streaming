import "./LoginPage.css";
import { register } from "../api/authApi";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SyntheticEvent } from "react";

function RegistrationPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [reenteredPassword, setReenteredPassword] = useState("");
    const navigate = useNavigate();

    const handleRegistration = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            await register(username, password, reenteredPassword);
            navigate("/");

        } catch (error) {
            console.error("Registration failed: ", error);
        }
    };

    return (
        <div className="login-container">
            <h1>Video Streaming</h1>
            <button className="back-button" onClick={() => navigate("/")}>
                Return to Login Page
            </button>
            <form onSubmit={handleRegistration}>
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

                <div>
                    <label htmlFor="reenteredPassword">Re-enter Password</label>
                    <input
                        id="reenteredPassword"
                        type="password"
                        value={reenteredPassword}
                        onChange={(event) => setReenteredPassword(event.target.value)}
                    />
                </div>

                <button type="submit">Register</button>
            </form>
        </div>
    );
}

export default RegistrationPage;