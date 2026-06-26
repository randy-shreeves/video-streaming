package com.randyshreeves.videostreaming.movie.dto;

public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private int releaseYear;
    private int runtimeMinutes;

    public MovieResponse(Long id, String title, String description, int releaseYear, int runtimeMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.runtimeMinutes = runtimeMinutes;
    }

    public Long getId() {return id;}

    public String getTitle() {return title;}

    public String getDescription() {return description;}

    public int getReleaseYear() {return releaseYear;}

    public int getRuntimeMinutes() {return runtimeMinutes;}
}
