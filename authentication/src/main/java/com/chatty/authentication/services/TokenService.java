package com.chatty.authentication.services;

import com.chatty.authentication.models.Token;
import com.chatty.authentication.repositories.TokenRepository;
import com.chatty.authentication.util.dto.user.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(refreshTokenName, null).path("/api/v1/auth").build();
    }

    public ResponseCookie generateRefreshTokenCookie(UserDTO user) {
        String refreshToken = generateRefreshToken(user);
        return ResponseCookie.from(refreshTokenName, refreshToken)
                .path("/api/v1/auth")
                .maxAge(refreshExpirationTimeMs)
                .httpOnly(true)
                .secure(true)
                .build();
    }

    public String resolveJWTFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(authHeaderName);
        if (authHeader == null ||!authHeader.startsWith(authHeaderStart)) {
            return null;
        }
        return authHeader.substring(authHeaderStart.length());
    }

    public String generateAccessToken(UserDTO user) {
        return buildToken(new HashMap<>(), user, secretAccess, accessExpirationTimeMs);
    }

    private String generateRefreshToken(UserDTO user) {
        String refreshToken = buildToken(new HashMap<>(), user, secretRefresh, refreshExpirationTimeMs);
        save(Token.builder()
                .token(refreshToken)
                .idUser(user.getId())
                .build());
        return refreshToken;
    }

    public boolean isTokenValid(String token, UserDTO user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDTO user,
            String secretKey,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities())
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