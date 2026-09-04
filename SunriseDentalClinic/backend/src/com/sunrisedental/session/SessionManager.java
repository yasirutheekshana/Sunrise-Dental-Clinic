package com.sunrisedental.session;

import com.sunrisedental.model.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side in-memory session manager.
 * Maps secure cryptographically unique session tokens to authenticated User objects.
 */
public class SessionManager {
    private static final long SESSION_TIMEOUT_SECONDS = 2 * 60 * 60; // 2 hours
    private static final SessionManager INSTANCE = new SessionManager();

    private static class SessionRecord {
        final User user;
        Instant lastAccessed;

        SessionRecord(User user) {
            this.user = user;
            this.lastAccessed = Instant.now();
        }

        void touch() {
            this.lastAccessed = Instant.now();
        }

        boolean isExpired() {
            return Instant.now().isAfter(lastAccessed.plusSeconds(SESSION_TIMEOUT_SECONDS));
        }
    }

    private final Map<String, SessionRecord> activeSessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new session for an authenticated user.
     *
     * @param user Authenticated user
     * @return Unique session token string
     */
    public String createSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot create session for null user");
        }
        String sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, new SessionRecord(user));
        return sessionId;
    }

    /**
     * Retrieves the active user associated with a given session token.
     * Returns null if session does not exist or has expired.
     *
     * @param sessionId Session token
     * @return Logged-in User or null
     */
    public User getUser(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return null;
        }
        SessionRecord record = activeSessions.get(sessionId.trim());
        if (record == null) {
            return null;
        }
        if (record.isExpired()) {
            activeSessions.remove(sessionId.trim());
            return null;
        }
        record.touch();
        return record.user;
    }

    /**
     * Terminates an active session upon logout.
     *
     * @param sessionId Session token
     */
    public void invalidateSession(String sessionId) {
        if (sessionId != null) {
            activeSessions.remove(sessionId.trim());
        }
    }

    /**
     * Clears all expired sessions periodically.
     */
    public void cleanExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Returns current count of active sessions.
     */
    public int getActiveSessionCount() {
        cleanExpiredSessions();
        return activeSessions.size();
    }
}
