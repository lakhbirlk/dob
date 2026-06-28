package com.dob;

import com.dob.infrastructure.security.JwtProvider;

/**
 * Test helper that creates a real JwtProvider with a fixed test secret.
 * Avoids Mockito issues with Java 25 bytecode incompatibility.
 */
public final class TestTokenProvider {

    private TestTokenProvider() {}

    private static final JwtProvider INSTANCE = new JwtProvider(
        "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-algo",
        "15m",
        "7d"
    );

    public static JwtProvider instance() {
        return INSTANCE;
    }
}
