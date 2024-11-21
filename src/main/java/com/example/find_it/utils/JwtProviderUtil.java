package com.example.find_it.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProviderUtil {
    private final SecretKey key;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.expiration.access}")
    private Long accessExpiration;

    public JwtProviderUtil(@Value("${jwt.secret}") String secretKey, UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(Authentication authentication) {
        Claims claims = Jwts.claims()
                .subject(authentication.getName())
                .build();

        long now = (new Date()).getTime();

        Date accessTokenExpiration = new Date(now + accessExpiration);
        return Jwts.builder()
                .claims(claims)
                .expiration(accessTokenExpiration)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) throws UsernameNotFoundException, JwtException {
        Claims claims = parseClaims(accessToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Claims parseClaims(String accessToken) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
    }
}

