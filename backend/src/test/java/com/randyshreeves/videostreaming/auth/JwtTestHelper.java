package com.randyshreeves.videostreaming.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtTestHelper {

    private final String secretKey;

    private String wrongKey = "thisIsNotTheRealSigningKey123456789012345678901234567890";

    public JwtTestHelper(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateExpiredToken(String username) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now().minusSeconds(120)))
                .expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenWithInvalidSignature(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(getWrongSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getWrongSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(wrongKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
