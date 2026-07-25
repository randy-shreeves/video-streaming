import { apiFetch } from "./apiClient";

export async function login(username: string, password: string) {
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