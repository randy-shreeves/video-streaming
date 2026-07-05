import type { Movie } from "../types/Movie";
const BASE_URL = "http://localhost:8080/movies";

export async function getMovies(): Promise<Movie[]> {
    const response = await fetch(BASE_URL);
    if (!response.ok) {
        throw new Error("Failed to fetch movies.");
    }
    const movies: Movie[] = await response.json();
    return movies;
}

export async function getMovie(id: number): Promise<Movie> {
    const response = await fetch(`${BASE_URL}/${id}`);
    if (!response.ok) {
        throw new Error("Failed to fetch movie.");
    }
    const movie: Movie = await response.json();
    return movie;
}