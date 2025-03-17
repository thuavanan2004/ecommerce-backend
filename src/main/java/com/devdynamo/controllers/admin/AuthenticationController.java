package com.devdynamo.controllers.admin;


import com.devdynamo.dtos.request.RegisterRequest;
import com.devdynamo.dtos.request.SignInRequest;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.SignInResponse;
import com.devdynamo.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        log.info("========================");
        return new ResponseEntity<>(authenticationService.signIn(signInRequest), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseData<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), authenticationService.register(registerRequest));
    }
}
