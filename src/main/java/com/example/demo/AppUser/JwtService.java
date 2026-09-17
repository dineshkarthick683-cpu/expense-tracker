package com.example.demo.AppUser;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey123456";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

// When you generate a JWT, the backend takes the header + payload,
// runs it through a hashing algorithm (HS256 in your case),
// and combines it with the secret.
//
// Example: a 256‑bit random key has 2^256 possible combinations —
// that’s astronomically huge. No computer can brute‑force it in realistic time.

// 1. Header
//    Small JSON object that says:
//    - Which algorithm was used to sign (HS256, RS256, etc.)
//    - That it’s a JWT

// 2. Payload
//    The actual data/claims about the user:
//    - sub → subject (username)
//    - iat → issued at
//    - exp → expiration
//    - role → your custom role claim

// 3. Signature
//    Created by hashing header + payload with your secret key.
//    Ensures nobody can tamper with the payload.
//    Example: dBjftJeZ4CVP-mB92K27uhbUJU1p...

    public String generateToken(String username,String role) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .claim("role", role)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getRole(String token){

            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().get("role",String.class);
    }

    //using getBody fetch get subject
    public boolean validateToken(String token, String username) {
        try {
            String tokenUser = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return (tokenUser.equals(username));
        } catch (Exception e) {
            return false; // invalid token
        }
    }


    // using getBody fetch get exp date
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
