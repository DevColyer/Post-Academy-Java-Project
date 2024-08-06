package com.sparta.midgard.security.api;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtManager {
    private static volatile JwtManager instance;

    @Value("${jwt.token.validity}")
    private long tokenValidity;
    private final SecretKey secretKey;

    private JwtManager(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Provides access to the singleton instance of JwtManager.
     *
     * If the instance does not already exist, it creates a new one using the provided `secretKey`.
     * This method ensures that only one instance of JwtManager is created, enforcing the singleton pattern.
     *
     * @param secretKey The key used for signing and verifying JWT tokens, typically defined in configuration (e.g., `application.properties`).
     * @return The singleton instance of JwtManager.
     */
    public static JwtManager getInstance(SecretKey secretKey) {
        if (instance == null) {
            synchronized (JwtManager.class) {
                if (instance == null) {
                    instance = new JwtManager(secretKey);
                }
            }
        }
        return instance;
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * This method retrieves the username embedded in the token's claims.
     *
     * @param token The JWT token from which the username is extracted.
     * @return The username extracted from the token.
     */
    public String extractUser(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date and time from the given JWT token.
     *
     * This method retrieves the expiration date of the token from its claims.
     *
     * @param token The JWT token from which the expiration date is extracted.
     * @return The expiration date and time extracted from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from the given JWT token.
     * @param token The JWT token from which the claim is to be extracted.
     * @param claimsResolver A function that defines how to extract the desired claim from the token's claims.
     * @param <T> The type of the claim to be extracted.
     * @return The value of the claim extracted from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

     /**
     * Generates a new JWT token based on the provided user details.
     *
     * This method creates a new token with claims derived from the provided `UserDetails`, including user permissions.
     * It sets the token's subject to the username and includes the permissions in the claims.
     *
     * @param userDetails The user details used to populate the token claims, including username and authorities.
     * @return A new JWT token with claims derived from the `UserDetails`.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("permissions", permissions);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(secretKey, SignatureAlgorithm.ES512)
                .compact();
    }

    /**
     * Validates the provided JWT token against the given user details.
     *
     * This method checks if the token's username matches the username in `userDetails` and ensures the token is not expired.
     *
     * @param token The JWT token to be validated.
     * @param userDetails The user details to validate the token against.
     * @return `true` if the token is valid and matches the user details, `false` otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUser(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Creates a new JWT token with an updated expiration date based on the provided token.
     *
     * This method extracts the claims from the existing token and generates a new token with the same claims but with
     * a new expiration date, effectively renewing the token's validity.
     *
     * @param token The JWT token to renew.
     * @return A new JWT token with an updated expiration date, retaining the same claims as the provided token.
     */
    public String renewToken(String token) {
        Claims claims = extractAllClaims(token);
        String subject = claims.getSubject();
        return createToken(claims, subject);
    }
}
