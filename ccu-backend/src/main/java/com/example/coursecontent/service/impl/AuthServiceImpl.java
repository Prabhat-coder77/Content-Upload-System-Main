package com.example.coursecontent.service.impl;

import com.example.coursecontent.service.AuthService;
import com.example.coursecontent.constants.AppConstants;

import com.example.coursecontent.domain.User;
import com.example.coursecontent.dto.request.LoginReq;
import com.example.coursecontent.dto.request.RegisterReq;
import com.example.coursecontent.dto.response.TokenRes;
import com.example.coursecontent.repository.UserRepository;
import com.example.coursecontent.security.JwtProvider;
import com.example.coursecontent.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenRes register(RegisterReq req, HttpServletRequest request) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(user);

        String jwtToken = jwtProvider.generateToken(user.getUsername(), "USER");
        String refreshToken = refreshTokenService.createRefreshToken(
                user, getClientIp(request), request.getHeader(AppConstants.HEADER_USER_AGENT));
                
        return TokenRes.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenRes login(LoginReq req, HttpServletRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String jwtToken = jwtProvider.generateToken(user.getEmail(), "USER");
        
        // Revoke all existing tokens for the user and issue a new one
        refreshTokenService.revokeAllUserTokens(user);
        String refreshToken = refreshTokenService.createRefreshToken(
                user, getClientIp(request), request.getHeader("User-Agent"));
                
        return TokenRes.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenRes refreshToken(String rawRefreshToken, HttpServletRequest request) {
        User user = refreshTokenService.verifyAndRevoke(rawRefreshToken);
        
        String jwtToken = jwtProvider.generateToken(user.getUsername(), "USER");
        String newRefreshToken = refreshTokenService.createRefreshToken(
                user, getClientIp(request), request.getHeader(AppConstants.HEADER_USER_AGENT));
                
        return TokenRes.builder()
                .accessToken(jwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isEmpty()) {
            refreshTokenService.verifyAndRevoke(rawRefreshToken);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader(AppConstants.HEADER_X_FORWARDED_FOR);
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
