const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
import type { LoginResponse } from "../types/LoginResponse";

export async function login(username: string, password: string): Promise<LoginResponse> {
    const response = await fetch(API_BASE_URL + "/auth/login", {
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
        const error = await response.json();
        throw new Error(error.message);
    }

    return response.json();
}

export async function register(username: string, password: string, reenteredPassword: string) {
    if (password !== reenteredPassword) {
        throw new Error("Passwords do not match.");
    }

    const response = await fetch(API_BASE_URL + "/auth/register", {
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
        const error = await response.json();
        throw new Error(error.message);
    }
}