package socialnetwork.repositories.impl;

import socialnetwork.models.Session;
import socialnetwork.repositories.SessionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of SessionRepository.
 */
public class InMemorySessionRepository implements SessionRepository {
    
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userSessionIndex = new ConcurrentHashMap<>();

    @Override
    public Session save(Session session) {
        sessions.put(session.getToken(), session);
        userSessionIndex.put(session.getUserId(), session.getToken());
        return session;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        Session session = sessions.get(token);
        if (session != null && session.isValid()) {
            session.updateLastAccess();
            return Optional.of(session);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Session> findActiveByUserId(String userId) {
        String token = userSessionIndex.get(userId);
        if (token == null) return Optional.empty();
        return findByToken(token);
    }

    @Override
    public void invalidate(String token) {
        Session session = sessions.get(token);
        if (session != null) {
            session.invalidate();
            userSessionIndex.remove(session.getUserId());
        }
    }

    @Override
    public void invalidateAllForUser(String userId) {
        sessions.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .forEach(s -> {
                    s.invalidate();
                    userSessionIndex.remove(userId);
                });
    }

    @Override
    public void deleteExpired() {
        sessions.entrySet().removeIf(entry -> {
            Session session = entry.getValue();
            if (session.isExpired()) {
                userSessionIndex.remove(session.getUserId());
                return true;
            }
            return false;
        });
    }
}



