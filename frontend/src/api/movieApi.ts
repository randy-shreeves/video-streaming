import type { Movie } from "../types/Movie";
import { apiFetch } from "./apiClient";

const BASE_URL = "/movies";

export async function getMovies(): Promise<Movie[]> {
    const response = await apiFetch(BASE_URL);
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}

export async function getMovie(id: number): Promise<Movie> {
    const response = await apiFetch(`${BASE_URL}/${id}`);
    if (!response.ok) {
        throw new Error("Failed to fetch movie.");
    }
    const movie: Movie = await response.json();
    return movie;
}

export async function getMoviePoster(id: number) {
    const response = await apiFetch(`${BASE_URL}/${id}/poster`);
    if (!response.ok) {
        throw new Error("Failed to load poster.");
    }
    const blob = await response.blob();
    return URL.createObjectURL(blob);
}