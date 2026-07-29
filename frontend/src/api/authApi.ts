import { apiFetch } from "./apiClient";
import type { LoginResponse } from "../types/LoginResponse";

export async function login(username: string, password: string): Promise<LoginResponse> {
    const response = await apiFetch("/auth/login", {
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

    return response.json();
}

export async function register(username: string, password: string, reenteredPassword: string) {
    if (password !== reenteredPassword) {
        throw new Error("Passwords do not match.");
    }

    const response = await apiFetch("/auth/register", {
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
        throw new Error("Registration failed.");
    }
}