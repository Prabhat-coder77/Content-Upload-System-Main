package com.example.coursecontent.service;

import com.example.coursecontent.domain.User;

public interface RefreshTokenService {
    String createRefreshToken(User user, String ipAddress, String userAgent);
    User verifyAndRevoke(String refreshToken);
    void revokeAllUserTokens(User user);
}
