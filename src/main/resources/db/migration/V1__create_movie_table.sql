CREATE TABLE movie (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT NOT NULL,
    runtime_minutes INT,
    storage_location VARCHAR(255) NOT NULL
);