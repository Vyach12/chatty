package ru.gusarov.messenger.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.models.Token;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.repositories.TokenRepository;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_START = "Bearer ";
    private static final int JWT_START_POSITION = AUTH_HEADER_START.length();
    private static final String REFRESH_TOKEN_NAME = "refresh_token";

    @Value("${application.security.jwt.access-token.secret-key}")
    private String secretAccess;

    @Value("${application.security.jwt.refresh-token.secret-key}")
    private String secretRefresh;

    @Value("${application.security.jwt.access-token.expiration}")
    private int jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    public void save(User user, String refreshToken) {
        tokenRepository.save(
                Token.builder()
                        .token(refreshToken)
                        .user(user)
                        .build()
        );
    }

    public void save(Token storedToken) {
        tokenRepository.save(storedToken);
    }

    public void delete(Token token) {
        tokenRepository.delete(token);
    }

    public Optional<Token> findByToken(String refreshToken) {
        return tokenRepository.findByToken(refreshToken);
    }

    public String createTokens(HttpServletResponse response, User user) {
        String refreshToken = generateRefreshToken(user);
        save(user, refreshToken);

        response.addCookie(
                createCookieRefreshToken(
                        refreshToken
                )
        );

        return generateAccessToken(user);
    }

    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = resolveTokenFromCookies(request);
        String username = extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Optional<Token> storedToken = findByToken(refreshToken);

        if (storedToken.isPresent() && isTokenValid(refreshToken, userDetails)) {
            refreshToken = generateRefreshToken(userDetails);
            String accessToken = generateAccessToken(userDetails);

            response.addCookie(createCookieRefreshToken(refreshToken));
            response.addHeader(AUTH_HEADER_NAME, AUTH_HEADER_START + accessToken);

            storedToken.get().setToken(refreshToken);

            save(storedToken.get());
            return accessToken;
        }
        return null;
    }

    private Cookie createCookieRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(refreshExpiration);
        cookie.setPath("/");

        return cookie;
    }

    public String resolveTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_NAME))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String resolveJWTFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader == null ||!authHeader.startsWith(AUTH_HEADER_START)) {
            return null;
        }
        return authHeader.substring(JWT_START_POSITION);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, secretAccess, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, secretRefresh, refreshExpiration);
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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
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
