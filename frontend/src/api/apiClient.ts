const BASE_URL = "http://localhost:8080";

export async function apiFetch(path: string, options: RequestInit = {}) {
    const token = localStorage.getItem("token");
    const headers = {
        ...options.headers,
        ...(token && {
            Authorization: `Bearer ${token}`
        })
    };

    return fetch(BASE_URL + path, {
        ...options,
        headers
    });
}