package com.chatty.chatsupport.services;

import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.token.WrongTypeTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${application.security.jwt.access-token.secret-key}")
    private String secretAccess;

    @Value("${application.http.auth-header-name}")
    private String authHeaderName;

    @Value("${application.http.auth-header-start}")
    private String authHeaderStart;

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getToken(String bearerToken) {
        if(!bearerToken.startsWith(authHeaderStart)) {
            throw WrongTypeTokenException.builder()
                    .errorCode(ErrorCode.UNSSUPPORTED_TOKEN)
                    .dataCausedError(bearerToken)
                    .errorMessage("Type of token is not bearer")
                    .errorDate(LocalDateTime.now())
                    .build();
        }
        return bearerToken.substring(authHeaderStart.length());
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
