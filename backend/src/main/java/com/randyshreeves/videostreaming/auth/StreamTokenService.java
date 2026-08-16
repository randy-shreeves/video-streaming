package com.randyshreeves.videostreaming.auth;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class StreamTokenService {

    private final String secretKey;
    private final long expiration;

    public StreamTokenService(
            @Value("${stream.token.secret}") String secretKey,
            @Value("${stream.token.expiration}") long expiration
    ) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String generateToken(Long movieId) {
        return Jwts.builder()
                .subject(movieId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, Long movieId) {
        final Long tokenMovieId = extractMovieId(token);
        return tokenMovieId.equals(movieId) && !isTokenExpired(token);
    }

    public Long extractMovieId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}