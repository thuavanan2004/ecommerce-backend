package com.devdynamo.services;

import com.devdynamo.enums.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token, TokenType type);

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetToken(UserDetails userDetails);

    boolean isTokenValid(String token, TokenType type, UserDetails userDetails);
}
