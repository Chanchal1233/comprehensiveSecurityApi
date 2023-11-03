package com.security.gas.plant.security.userconfiguration;

import com.security.gas.plant.service.RedisTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Value("${application.security.jwt.private-key}")
    private String privateKeyString;

    @Value("${application.security.jwt.public-key}")
    private String publicKeyString;

    private final RedisTokenService redisTokenService;
    private final RedisTemplate redisTemplate;

    private PrivateKey getSignInKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get the private key", e);
        }
    }

    private PublicKey getVerificationKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get the public key", e);
        }
    }

    public boolean isValidSignature(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getVerificationKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return false;
        }
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        if (roles.contains("ROLE_SUPER-ADMIN")) {
            claims.put("roles", Collections.singletonList("ROLE_SUPER-ADMIN"));
        } else {
            List<String> permissions = userDetails.getAuthorities()
                    .stream()
                    .filter(grantedAuthority -> !grantedAuthority.getAuthority().startsWith("ROLE_"))
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            roles.forEach(role -> {
                System.out.println("Role: " + role);
            });
            permissions.forEach(permission -> {
                System.out.println("Permission: " + permission);
            });
            claims.put("roles", roles);
            claims.put("permissions", permissions);
        }
        String existingToken = redisTokenService.getTokenForUser(userDetails.getUsername());
        if (existingToken != null) {
            log.info("Existing token found for user {}. Invalidating it.", userDetails.getUsername());
            redisTokenService.invalidateToken(existingToken);
        }
        String token = buildToken(claims, userDetails, jwtExpiration);
        redisTokenService.saveTokenToRedis(token, userDetails, jwtExpiration);
        return token;
    }

    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class) == null ? Collections.emptyList() : claims.get("roles", List.class);
    }

    public List<String> extractPermissions(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class) == null ? Collections.emptyList() : claims.get("permissions", List.class);
    }



    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Date getExpiration(String token) {
        return extractExpiration(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}