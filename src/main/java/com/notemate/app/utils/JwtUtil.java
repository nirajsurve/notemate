package com.notemate.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private String SECRET_KEY = "6cd85e2a9fe0c187a7406b46c04e8bdadd4d3079eb02919ef69e12d0a7c3531632974aaf55e33fc84c998e4885b44a1025934e02e7f75e2bcb31d59afc90183266f35e58e0856772d922dd50b3a1afe17f7e32cd55488f0eb924b1a65b3d4e1e67029c580e03610351d9a4994ea98d0ff74c6b986cf04e6ca6bc6938bf78707a1539f3bee71428f40241062fed48ac0c5cc231d7b439ed7e0acc04a2a503d429e397ea8bcd82c56762cc627d0f8960479475a2a06a2c91e036941366445941e6a19ff0ec4b89b48cee0ed749f32df507f3e731ca6e329ce170368c25cd839d041922b084863cc425b1911a9a1a632a0f4b8f994d8515b78e68f3380d3c1698cf";

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, username);
    }

    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // token valid for 1 hour
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
