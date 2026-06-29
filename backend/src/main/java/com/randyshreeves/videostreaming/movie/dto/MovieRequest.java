package com.randyshreeves.videostreaming.movie.dto;

import jakarta.validation.constraints.*;

public class MovieRequest {

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 255, message = "Title cannot be greater than 255 characters.")
    private String title;

    @Size(max = 1000, message = "Description cannot be greater than 1000 characters.")
    private String description;

    @NotNull(message = "Release year is required.")
    @Min(value = 1888, message = "Release year must be after 1887.")
    private Integer releaseYear;

    @NotNull(message = "Runtime is required.")
    @Positive(message = "Runtime must be greater than 0.")
    private Integer runtimeMinutes;

    @NotBlank(message = "Storage location cannot be blank.")
    @Size(max = 255, message = "Storage location cannot be greater than 255 characters.")
    private String storageLocation;

    public MovieRequest() {}

    public MovieRequest(String title, String description, int releaseYear, int runtimeMinutes, String storageLocation) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.runtimeMinutes = runtimeMinutes;
        this.storageLocation = storageLocation;
    }

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public int getReleaseYear() {return releaseYear;}

    public void setReleaseYear(int releaseYear) {this.releaseYear = releaseYear;}

    public int getRuntimeMinutes() {return runtimeMinutes;}

    public void setRuntimeMinutes(int runtimeMinutes) {this.runtimeMinutes = runtimeMinutes;}

    public String getStorageLocation() {return storageLocation;}

    public void setStorageLocation(String storageLocation) {this.storageLocation = storageLocation;}
}
