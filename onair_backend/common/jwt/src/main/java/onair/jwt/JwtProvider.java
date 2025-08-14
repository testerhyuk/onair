package onair.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private final long accessTokenValidity = 1000 * 60 * 15; // 15분
    private final long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7일

    public String generateAccessToken(String memberId, String role) {
        return Jwts.builder()
                .setSubject(memberId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(
                        Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                ).compact();
    }

    public String generateRefreshToken(String memberId) {
        return Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(
                        Keys.hmacShaKeyFor(jwtSecretKey.getBytes()),
                        SignatureAlgorithm.HS256
                ).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getMemberFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
