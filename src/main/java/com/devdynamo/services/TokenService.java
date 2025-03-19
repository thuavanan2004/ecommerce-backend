package com.devdynamo.services;

import com.devdynamo.entities.TokenEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.repositories.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public TokenEntity getByEmail(String email) {
        return tokenRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Not found token"));
    }

    public Long save(TokenEntity token) {
        Optional<TokenEntity> optional = tokenRepository.findByEmail(token.getEmail());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            TokenEntity t = optional.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            return t.getId();
        }
    }

    public void delete(String email) {
        TokenEntity token = getByEmail(email);
        tokenRepository.delete(token);
    }
}
