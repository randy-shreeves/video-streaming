package com.randyshreeves.videostreaming.movie;

import jakarta.persistence.*;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private int releaseYear;

    private int runtimeMinutes;

    private String storageLocation;

    private String posterLocation;

    @Column(nullable = false)
    private boolean published;

    public Movie(){}

    public Movie(String title,
                 String description,
                 int releaseYear,
                 int runtimeMinutes
    ) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.runtimeMinutes = runtimeMinutes;
        this.published = false;
    }

    public Long getId() {return id;}

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

    public String getPosterLocation() {
        return posterLocation;
    }

    public void setPosterLocation (String posterLocation) {
        this.posterLocation = posterLocation;
    }

    public boolean isPublished() {return published;}

    public void setPublished(boolean published) {
        this.published = published;
    }
}
