import type { Movie } from "../types/Movie";
import { apiFetch } from "./apiClient";

const BASE_URL = "/movies";

export async function getMovies(signal?: AbortSignal): Promise<Movie[]> {
    const response = await apiFetch(BASE_URL, { signal });
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}

export async function getMovie(id: number, signal?: AbortSignal): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/${id}`, { signal });
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

export async function getMovieStream(id: number, signal?: AbortSignal) {
    const response = await apiFetch(`${BASE_URL}/${id}/stream`, { signal })
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    const blob = await response.blob();
    return URL.createObjectURL(blob);
}