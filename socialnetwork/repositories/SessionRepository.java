package socialnetwork.repositories;

import socialnetwork.models.Session;

import java.util.Optional;

/**
 * Repository interface for Session data access.
 */
public interface SessionRepository {
    
    Session save(Session session);
    
    Optional<Session> findByToken(String token);
    
    Optional<Session> findActiveByUserId(String userId);
    
    void invalidate(String token);
    
    void invalidateAllForUser(String userId);
    
    void deleteExpired();
}



