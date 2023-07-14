package ru.gusarov.messenger.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.gusarov.messenger.services.TokenService;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_START = "Bearer ";
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            Optional<String> accessToken  = tokenService.resolveJWTFromRequest(request);
            if(accessToken.isEmpty()) {
                return;
            }
            String username = tokenService.extractUsername(accessToken.get());

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (tokenService.isTokenValid(accessToken.get(), userDetails)) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (ExpiredJwtException | SignatureException | IllegalArgumentException e) {
            try {
                tokenService.refresh(request, response);
            } catch (ExpiredJwtException | SignatureException | IllegalArgumentException exception) {
                System.out.println("ssssssssssssssssssss");
            }
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
