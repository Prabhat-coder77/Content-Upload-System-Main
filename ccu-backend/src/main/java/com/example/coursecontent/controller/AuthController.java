package com.example.coursecontent.controller;

import com.example.coursecontent.dto.request.LoginReq;
import com.example.coursecontent.dto.request.RefreshTokenReq;
import com.example.coursecontent.dto.request.RegisterReq;
import com.example.coursecontent.dto.response.StandardResponse;
import com.example.coursecontent.dto.response.TokenRes;
import com.example.coursecontent.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<TokenRes>> register(@Valid @RequestBody RegisterReq requestDTO, HttpServletRequest request) {
        return ResponseEntity.ok(StandardResponse.success("User registered successfully", authService.register(requestDTO, request)));
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse<TokenRes>> login(@Valid @RequestBody LoginReq requestDTO, HttpServletRequest request) {
        return ResponseEntity.ok(StandardResponse.success("Login successful", authService.login(requestDTO, request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<TokenRes>> refresh(@Valid @RequestBody RefreshTokenReq requestDTO, HttpServletRequest request) {
        return ResponseEntity.ok(StandardResponse.success("Token refreshed successfully", authService.refreshToken(requestDTO.getRefreshToken(), request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<StandardResponse<Void>> logout(@Valid @RequestBody RefreshTokenReq requestDTO) {
        authService.logout(requestDTO.getRefreshToken());
        return ResponseEntity.ok(StandardResponse.success("Logged out successfully", null));
    }
}
