package com.musclebuilder.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return getDecodedJwt(token).getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(getAlgorithm());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private boolean isTokenExpired(String token) {
        return getDecodedJwt(token).getExpiresAt().before(new Date());
    }

    private DecodedJWT getDecodedJwt(String token) {
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }


}
