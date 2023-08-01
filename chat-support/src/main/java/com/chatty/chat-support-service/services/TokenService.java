package ru.gusarov.messenger.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import ru.gusarov.messenger.models.Token;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.repositories.TokenRepository;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.token.TokenNotFoundException;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${application.http.auth-header-name}")
    private String authHeaderName;

    @Value("${application.http.auth-header-start}")
    private String authHeaderStart;

    @Value("${application.http.refresh-token-name}")
    private  String refreshTokenName;

    @Value("${application.security.jwt.access-token.secret-key}")
    private String secretAccess;

    @Value("${application.security.jwt.refresh-token.secret-key}")
    private String secretRefresh;

    @Value("${application.security.jwt.access-token.expiration}")
    private int accessExpirationTimeMs;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpirationTimeMs;
    private final TokenRepository tokenRepository;
    private final UserService userService;

    public void save(Token storedToken) {
        tokenRepository.save(storedToken);
    }

    public void delete(String refreshToken) {
        Optional<Token> token = tokenRepository.findByToken(refreshToken);
        token.ifPresent(tokenRepository::delete);
    }

    public Optional<Token> findByToken(String refreshToken) {
        return tokenRepository.findByToken(refreshToken);
    }

    public ResponseCookie refresh(String refreshToken) {
        Optional<Token> storedToken = findByToken(refreshToken);
        if(storedToken.isEmpty()) {
            throw TokenNotFoundException.builder()
                    .errorCode(ErrorCode.TOKEN_NOT_FOUND)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(refreshToken)
                    .errorMessage("Token not found")
                    .build();
        }

        String username = extractUsername(refreshToken);
        User user = userService.findByUsername(username);

        if (isTokenValid(refreshToken, user)) {
            tokenRepository.delete(storedToken.get());
            return generateRefreshTokenCookie(user);
        }
        return null;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(refreshTokenName, null).path("/api/v1/auth").build();
    }

    public ResponseCookie generateRefreshTokenCookie(UserDetails userDetails) {
        String refreshToken = generateRefreshToken(userDetails);
        return ResponseCookie.from(refreshTokenName, refreshToken)
                .path("/api/v1/auth")
                .maxAge(refreshExpirationTimeMs)
                .httpOnly(true)
                .secure(true)
                .build();
    }


    public String resolveTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenName);
        if(cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public String resolveJWTFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(authHeaderName);
        if (authHeader == null ||!authHeader.startsWith(authHeaderStart)) {
            return null;
        }
        return authHeader.substring(authHeaderStart.length());
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, secretAccess, accessExpirationTimeMs);
    }

    private String generateRefreshToken(UserDetails userDetails) {
        String refreshToken = buildToken(new HashMap<>(), userDetails, secretRefresh, refreshExpirationTimeMs);
        save(Token.builder()
                .token(refreshToken)
                .user(userService.findByUsername(userDetails.getUsername()))
                .build());
        return refreshToken;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            String secretKey,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretAccess))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
