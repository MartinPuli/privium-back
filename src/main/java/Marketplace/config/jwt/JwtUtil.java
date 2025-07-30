package Marketplace.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import Marketplace.models.User;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyBase64;
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

   public String generateToken(User user) {
    // Armamos un Claims personalizado para incluir toda la info
    Claims claims = Jwts.claims().setSubject(user.getEmail());
    claims.put("userId",       user.getId());
    claims.put("name",         user.getName());
    claims.put("lastname",     user.getLastName());
    claims.put("dni",          user.getDni());
    claims.put("email",        user.getEmail());
    claims.put("countryId",    user.getCountryId());
    claims.put("role",         user.getRole());
    claims.put("contactPhone", user.getContactPhone());

    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86_400_000)) // 1 día
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

}

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }
}
