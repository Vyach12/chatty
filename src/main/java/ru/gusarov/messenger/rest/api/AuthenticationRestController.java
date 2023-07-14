package ru.gusarov.messenger.rest.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.services.AuthenticationService;
import ru.gusarov.messenger.services.TokenService;
import ru.gusarov.messenger.util.dto.authentication.AuthenticationRequest;
import ru.gusarov.messenger.util.dto.authentication.RegisterRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public HttpStatus register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        tokenService.updateTokens(
                response, authService.register(request)
        );

        return HttpStatus.CREATED;
    }

    @PostMapping("/authenticate")
    public HttpStatus authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ){
        tokenService.updateTokens(
                response, authService.authenticate(request)
        );

        return HttpStatus.OK;
    }

    @PostMapping("/logout")
    public HttpStatus logout(
            @CookieValue("refresh_token") String refreshToken
    ) {
        authService.logout(refreshToken);
        return HttpStatus.OK;
    }

    @PostMapping("/refresh")
    public HttpStatus refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        tokenService.refresh(request, response);
        return HttpStatus.OK;
    }
}
