package com.randyshreeves.videostreaming.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class StreamTokenAuthenticationFilter extends OncePerRequestFilter {

    private final StreamTokenService streamTokenService;

    public StreamTokenAuthenticationFilter(StreamTokenService streamTokenService) {
        this.streamTokenService = streamTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (requestUri.matches("/movies/\\d+/stream")) {
            String token = request.getParameter("token");
            if (token == null || token.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String movieIdString = requestUri.replaceAll(".*/movies/(\\d+)/stream", "$1");
            Long movieId = Long.valueOf(movieIdString);
            try {
                if (!streamTokenService.isTokenValid(token, movieId)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                        "stream-token",
                        null,
                        AuthorityUtils.NO_AUTHORITIES
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
