package com.example.HotelBooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j

public class JwtUtils {
    private static final long EXPIRATION_TIME_IN_MILSEC = 100L * 60L * 60L * 24L * 30L *6L;
    private SecretKey secretKey;
    @Value("${secretJwtString}")
    private String secretJwtString;

    @PostConstruct
    public void init() {
        byte[] keybyte = secretJwtString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keybyte, "HmacSHA256");
        secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretJwtString.getBytes());
        log.info("Secret key initialized for JWT: {}", secretKey);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILSEC))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    private <T> T extractClaims(String token, java.util.function.Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
