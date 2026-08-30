import type { Movie } from "./Movie";

export interface MoviePage {
    content: Movie[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}