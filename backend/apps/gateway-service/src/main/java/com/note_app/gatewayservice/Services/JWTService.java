package com.note_app.gatewayservice.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService implements IJwtService {

    private static final String SECRET_KEY = "your_very_secret_and_very_long_random_key_here";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public record DecodedToken(String userId, String email, String role) {}

    public DecodedToken verifyAndDecode(String token) {
        Claims claims = extractAllClaims(token);

        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new IllegalArgumentException("Token expired");
        }

        String email = claims.getSubject();
        if (email == null) {
            email = claims.get("email", String.class);
        }
        String role = claims.get("role", String.class);
        String userId = claims.get("userId", String.class);
        if (userId == null) {
            userId = claims.get("id", String.class);
        }

        return new DecodedToken(userId, email, role);
    }


    @Override
    public boolean isTokenValid(String token) {
        try {
            verifyAndDecode(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
