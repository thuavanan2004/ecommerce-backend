package com.devdynamo.controllers.admin;


import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.request.ResetPasswordDTO;
import com.devdynamo.dtos.request.SignInRequest;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.SignInResponse;
import com.devdynamo.dtos.response.TokenResponse;
import com.devdynamo.services.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name="Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/access-token")
    public ResponseEntity<TokenResponse> accessToken(@Valid @RequestBody SignInRequest signInRequest) {
        log.info("========================");
        return new ResponseEntity<>(authenticationService.accessToken(signInRequest), OK);
    }

    @PostMapping("/register")
    public ResponseData<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseData<>(OK.value(), authenticationService.register(registerRequest));
    }

    @PostMapping("/refresh-token")
    public  ResponseEntity<TokenResponse>  refreshToken(HttpServletRequest request){
        return new ResponseEntity<>(authenticationService.refreshToken(request), OK);
    }

    @PostMapping("/remove-token")
    public ResponseEntity<String> removeToken(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.removeToken(request), OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        return new ResponseEntity<>(authenticationService.forgotPassword(email), OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody String secretKey) {
        return new ResponseEntity<>(authenticationService.resetPassword(secretKey), OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ResetPasswordDTO request) {
        return new ResponseEntity<>(authenticationService.changePassword(request), OK);
    }
}
