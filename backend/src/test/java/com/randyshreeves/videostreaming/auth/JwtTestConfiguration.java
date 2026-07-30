package com.randyshreeves.videostreaming.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JwtTestConfiguration {

    @Bean
    JwtTestHelper jwtTestHelper(@Value("${jwt.secret}") String secretKey) {
        return new JwtTestHelper(secretKey);
    }
}
