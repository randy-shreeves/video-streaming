import type { Movie } from "../types/Movie";
import type { MovieRequest } from "../types/MovieRequest";
import { apiFetch } from "./apiClient";

const BASE_URL = "/movies";

export async function getAllMovies(signal?: AbortSignal): Promise<Movie[]> {
    const response = await apiFetch(`${BASE_URL}/admin`, { signal });
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}

export async function getPublishedMovies(signal?: AbortSignal): Promise<Movie[]> {
    const response = await apiFetch(BASE_URL, { signal });
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}

export async function getMovie(id: number, signal?: AbortSignal): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/${id}/details`, { signal });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    const movie: Movie = await response.json();
    return movie;
}

export async function getMoviePoster(id: number, signal?: AbortSignal) {
    const response = await apiFetch(`${BASE_URL}/${id}/poster`, { signal });
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
    const response = await apiFetch("/movies", {
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
    const response = await apiFetch(`/movies/${id}/poster`, {
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
    const response = await apiFetch(`/movies/${id}/video`, {
        method: "POST",
        body: formData
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function updateMovie(id: number, movieRequest: MovieRequest): Promise<Movie> {
    const response = await apiFetch(`/movies/${id}`, {
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
    const response = await apiFetch(`/movies/${id}`, {
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