package com.devdynamo.services;

import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.request.ResetPasswordDTO;
import com.devdynamo.dtos.request.SignInRequest;
import com.devdynamo.dtos.response.TokenResponse;
import com.devdynamo.entities.RedisToken;
import com.devdynamo.entities.TokenEntity;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.enums.Role;
import com.devdynamo.enums.UserStatus;
import com.devdynamo.exceptions.InvalidDataException;
import com.devdynamo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.devdynamo.enums.TokenType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final TokenService tokenService;
    private final RedisTokenService redisTokenService;
    private final EmailService emailService;


    public TokenResponse accessToken(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.save(TokenEntity.builder().email(user.getEmail()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public String register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        var user = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(UserStatus.active)
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .role(Role.customer)
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    public TokenResponse refreshToken(HttpServletRequest request){
        final String refreshToken = request.getHeader("token");
        if(refreshToken == null || refreshToken.isEmpty()){
            throw new InvalidDataException("Token must be not blank");
        }
        final String email = jwtService.extractUserName(refreshToken, REFRESH_TOKEN);
        var user = userService.getUserByEmail(email);
        if(!jwtService.isTokenValid(refreshToken, REFRESH_TOKEN, user)){
            throw new InvalidDataException("Not allow access with this token");
        }
        String accessToken = jwtService.generateAccessToken(user);

        tokenService.save(TokenEntity.builder().email(user.getEmail()).accessToken(accessToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public String removeToken(HttpServletRequest request) {
        log.info("---------- logout ----------");

        final String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String email = jwtService.extractUserName(token, ACCESS_TOKEN);
        tokenService.delete(email);

        return "Deleted!";
    }

    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        UserEntity user = userService.getUserByEmail(email);

        String resetToken = jwtService.generateResetToken(user);

        redisTokenService.save(RedisToken.builder().id(user.getUsername()).resetToken(resetToken).build());

        String resetLink = "http://localhost:8080/api/admin/auth/reset-password?token=" + resetToken;
        emailService.sendResetPasswordEmail(email, resetLink);

        log.info("--> confirmLink: {}", resetLink);

        return resetToken;
    }

    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");

        var user = validateToken(secretKey);

        tokenService.getByEmail(user.getEmail());

        return "Reset";
    }

    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        var user = validateToken(request.getSecretKey());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
        return "Changed";
    }

    private UserEntity validateToken(String token) {
        var email = jwtService.extractUserName(token, RESET_TOKEN);

        redisTokenService.isExist(email);

        var user = userService.getUserByEmail(email);
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }

        return user;
    }
}
