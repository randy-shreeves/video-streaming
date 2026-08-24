const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function apiFetch(path: string, options: RequestInit = {}) {
    const token = localStorage.getItem("token");
    const headers = {
        ...options.headers,
        ...(token && {
            Authorization: `Bearer ${token}`
        })
    };

    return fetch(API_BASE_URL + path, {
        ...options,
        headers
    });
}