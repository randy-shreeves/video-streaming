import { apiFetch } from "./apiClient";
import type { MoviePage } from "../types/MoviePage";

export async function getWatchlist(
    page: number, 
    size: number, 
    signal?: AbortSignal
): Promise<MoviePage> {
    const params = new URLSearchParams();
    params.set("page", page.toString());
    params.set("size", size.toString());
    const response = await apiFetch(`/watchlist?${params.toString()}`, {signal})
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
    return response.json();
}

export async function addToWatchlist(movieId: number, signal?: AbortSignal): Promise<void> {
    const response = await apiFetch(`/watchlist/${movieId}`, {
        method: "POST",
        signal
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}

export async function removeFromWatchlist(movieId: number, signal?: AbortSignal): Promise<void> {
    const response = await apiFetch(`/watchlist/${movieId}`, {
        method: "DELETE",
        signal
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message);
    }
}