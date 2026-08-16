package com.randyshreeves.videostreaming.auth;

import com.randyshreeves.videostreaming.movie.StreamTokenService;
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
            String movieIdString = requestUri.replaceAll(".*/movies/(\\d+)/stream", "$1");
            Long movieId = Long.valueOf(movieIdString);
            boolean valid = streamTokenService.isTokenValid(token, movieId);

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            "stream-token-authenticated",
                            null,
                            AuthorityUtils.NO_AUTHORITIES
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
