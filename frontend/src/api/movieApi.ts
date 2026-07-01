import type { Movie } from "../types/Movie"
const BASE_URL = "http://localhost:8080/movies";

export async function getMovies(): Promise<Movie[]> {
    const response = await fetch(BASE_URL);
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}