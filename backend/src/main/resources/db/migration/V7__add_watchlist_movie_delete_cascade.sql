ALTER TABLE watchlist
DROP CONSTRAINT fk_watchlist_movie;

ALTER TABLE watchlist
ADD CONSTRAINT fk_watchlist_movie
    FOREIGN KEY (movie_id)
    REFERENCES movie(id)
    ON DELETE CASCADE;