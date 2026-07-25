import "./LoginPage.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SyntheticEvent } from "react";

function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (event: SyntheticEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username,
                    password
                })
            });

            if (!response.ok) {
                throw new Error("Invalid username or password.");
            }

            const data = await response.json();
            localStorage.setItem("token", data.token);
            navigate("/movies");

        } catch (error) {
            console.error("Login failed: ", error);
        }
    };

    return (
        <div className="login-container">
            <h1>Video Streaming</h1>
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

                <button type="submit">Log In</button>
            </form>
        </div>
    );
}

export default LoginPage;