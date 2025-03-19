package com.devdynamo.services.Impl;

import com.devdynamo.enums.TokenType;
import com.devdynamo.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.devdynamo.enums.TokenType.*;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.resetKey}")
    private String resetKey;

    @Override
    public boolean isTokenValid(String token, TokenType type, UserDetails userDetails) {
        final String userName = extractUserName(token, type);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token, type);
    }

    private boolean isTokenExpired(String token, TokenType type) {
        return extractExpiration(token, type).before(new Date());
    }

    private Date extractExpiration(String token, TokenType type) {
        return extractClaim(token, type, Claims::getExpiration);
    }


    @Override
    public String extractUserName(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token, type);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey(type)).build().parseClaimsJws(token).getBody();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateResetToken(UserDetails userDetails) {
        return generateResetToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 1000))
                .signWith(getSigningKey(ACCESS_TOKEN), SignatureAlgorithm.HS256).compact();
    }
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSigningKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateResetToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSigningKey(RESET_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey(TokenType type) {
        if(ACCESS_TOKEN.equals(type)){
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        }else if (REFRESH_TOKEN.equals(type)){
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
        }else {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
        }
    }

}
