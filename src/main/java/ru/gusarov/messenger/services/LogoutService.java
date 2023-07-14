package ru.gusarov.messenger.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenService tokenService;
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        final Optional<String> refreshToken = tokenService.resolveTokenFromCookies(request);
        if(refreshToken.isPresent()) {
            var storedToken = tokenService.findByToken(refreshToken.get());
            if (storedToken.isPresent()) {
                tokenService.delete(storedToken.get());
                SecurityContextHolder.clearContext();
            }
        }
    }
}
