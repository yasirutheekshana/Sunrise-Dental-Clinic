package com.sunrisedental.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for secure BCrypt password hashing and verification.
 * Follows industry best practices: passwords are salted and hashed; never stored or logged in plain text.
 */
public final class PasswordUtil {
    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    /**
     * Hashes a plain-text password using BCrypt with a secure salt.
     *
     * @param plainPassword Raw password string
     * @return Secure salted BCrypt hash string
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty for hashing");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword  Raw password entered by user
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
