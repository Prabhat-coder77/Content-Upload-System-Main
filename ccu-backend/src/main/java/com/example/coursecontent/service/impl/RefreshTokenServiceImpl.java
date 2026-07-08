package com.example.coursecontent.service.impl;

import com.example.coursecontent.domain.RefreshToken;
import com.example.coursecontent.domain.User;
import com.example.coursecontent.exception.UnauthorizedException;
import com.example.coursecontent.repository.RefreshTokenRepository;
import com.example.coursecontent.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.security.jwt.refreshExpirationDays}")
    private long refreshTokenDurationDays;

    @Override
    @Transactional
    public String createRefreshToken(User user, String ipAddress, String userAgent) {
        // Generate a random raw token
        String rawToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        
        // Hash it for db storage
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Override
    @Transactional
    public User verifyAndRevoke(String rawToken) {
        String tokenHash = hashToken(rawToken);
        
        RefreshToken tokenConfig = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (tokenConfig.isExpired()) {
            tokenConfig.setRevokedAt(Instant.now());
            refreshTokenRepository.save(tokenConfig);
            throw new UnauthorizedException("Refresh token was expired. Please make a new signin request");
        }
        
        if (tokenConfig.isRevoked()) {
            // Security measure: if a revoked token is used, compromise is likely. 
            // Revoke all tokens for the user.
            revokeAllUserTokens(tokenConfig.getUser());
            throw new UnauthorizedException("Invalid refresh token (revoked)");
        }

        // Revoke the current token as it's being used successfully
        // We will issue a new one in the AuthService exchange
        tokenConfig.setRevokedAt(Instant.now());
        refreshTokenRepository.save(tokenConfig);

        return tokenConfig.getUser();
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> validTokens = refreshTokenRepository.findAllByUserAndRevokedAtIsNull(user);
        if (!validTokens.isEmpty()) {
            validTokens.forEach(t -> t.setRevokedAt(Instant.now()));
            refreshTokenRepository.saveAll(validTokens);
        }
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash token", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
