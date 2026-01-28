package org.example.bank_rest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMinutes;

    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES))
                .withIssuer("bank_rest")
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndGetUsername(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("bank_rest")
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
}
