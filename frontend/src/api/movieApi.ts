import type { Movie } from "../types/Movie";
import type { MoviePage } from "../types/MoviePage";
import type { MovieRequest } from "../types/MovieRequest";
import { apiFetch } from "./apiClient";

const BASE_URL = "/movies";

export async function getAllMovies(
    search?: string,
    page = 0,
    size = 12,
    signal?: AbortSignal
): Promise<MoviePage> {
    const params = new URLSearchParams();
    if (search?.trim()) {
        params.set("search", search.trim());
    }
    params.set("page", page.toString());
    params.set("size", size.toString());
    const response = await apiFetch(`${BASE_URL}/admin?${params.toString()}`, {signal})
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    return response.json();
}

export async function getPublishedMovies(
    search?: string,
    page = 0,
    size = 12, 
    signal?: AbortSignal
): Promise<MoviePage> {
    const params = new URLSearchParams();
    if (search?.trim()) {
        params.set("search", search.trim());
    }
    params.set("page", page.toString());
    params.set("size", size.toString());
    const response = await apiFetch(`${BASE_URL}?${params.toString()}`, {signal})
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    return response.json();
}

export async function getMovie(id: number, signal?: AbortSignal): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/admin/${id}/details`, { signal });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    const movie: Movie = await response.json();
    return movie;
}

export async function getPublishedMovie(id: number, signal?: AbortSignal): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/${id}/details`, { signal });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    const movie: Movie = await response.json();
    return movie;
}

export async function getPublishedMoviePoster(id: number, signal?: AbortSignal) {
    const response = await apiFetch(`${BASE_URL}/${id}/poster`, { signal });
    if (!response.ok) {
        throw new Error("Failed to load poster.");
    }
    const blob = await response.blob();
    return URL.createObjectURL(blob);
}

export async function getMoviePoster(id: number, signal?: AbortSignal) {
    const response = await apiFetch(`${BASE_URL}/admin/${id}/poster`, { signal });
    if (!response.ok) {
        throw new Error("Failed to load poster.");
    }
    const blob = await response.blob();
    return URL.createObjectURL(blob);
}

export async function getStreamToken(id: number, signal?: AbortSignal): Promise<string> {
    const response = await apiFetch(`${BASE_URL}/${id}/stream-token`, {
        signal
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    return response.text();
}

export async function createMovie(movieRequest: MovieRequest): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(movieRequest)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }

    return response.json();
}

export async function uploadPoster(id: number, poster: File) {
    const formData = new FormData();
    formData.append("poster", poster);
    const response = await apiFetch(`${BASE_URL}/${id}/poster`, {
        method: "POST",
        body: formData
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function uploadVideo(id: number, video: File) {
    const formData = new FormData();
    formData.append("video", video);
    const response = await apiFetch(`${BASE_URL}/${id}/video`, {
        method: "POST",
        body: formData
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function updateMovie(id: number, movieRequest: MovieRequest): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(movieRequest)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }

    return response.json();
}

export async function deleteMovie(id: number): Promise<void> {
    const response = await apiFetch(`${BASE_URL}/${id}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function publishMovie(id: number): Promise<void> {
    const response = await apiFetch(`${BASE_URL}/${id}/publish`, {
        method: "POST"
    });

    if(!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function unpublishMovie(id: number): Promise<void> {
    const response = await apiFetch(`${BASE_URL}/${id}/unpublish`, {
        method: "POST"
    });

    if(!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}