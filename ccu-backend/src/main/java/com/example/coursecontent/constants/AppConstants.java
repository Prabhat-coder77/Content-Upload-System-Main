package com.example.coursecontent.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // Standard HTTP Header Names
    public static final String HEADER_AUTHORIZATION   = "Authorization";
    public static final String HEADER_USER_AGENT      = "User-Agent";
    public static final String HEADER_ACCEPT          = "Accept";

    // Proxy / Forwarding Headers
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_X_REAL_IP       = "X-Real-IP";

    // Auth Scheme
    /** Prefix for Bearer token values (includes trailing space). */
    public static final String BEARER_PREFIX          = "Bearer ";

    // Media Types
    public static final String APPLICATION_JSON       = "application/json";

    // JWT Claim Keys
    public static final String CLAIM_EMAIL            = "email";

    // Cookie Names
    public static final String COOKIE_ACCESS_TOKEN    = "accessToken";
    public static final String COOKIE_REFRESH_TOKEN   = "refreshToken";

    // Crypto Algorithms
    public static final String HASH_ALGORITHM_SHA256  = "SHA-256";

    // CORS Configuration Defaults
    public static final String[] ALLOWED_CORS_METHODS = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
    public static final String[] ALLOWED_CORS_HEADERS = {"Authorization", "Content-Type", "Accept", "X-Requested-With"};
}
