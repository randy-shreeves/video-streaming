CREATE TABLE watchlist (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,

    CONSTRAINT fk_watchlist_user
       FOREIGN KEY (user_id)
       REFERENCES users(id),

    CONSTRAINT fk_watchlist_movie
       FOREIGN KEY (movie_id)
       REFERENCES movie(id),

    CONSTRAINT uq_watchlist_user_movie
    UNIQUE (user_id, movie_id)
);