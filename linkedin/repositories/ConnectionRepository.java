package linkedin.repositories;

import linkedin.enums.ConnectionStatus;
import linkedin.models.Connection;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends Repository<Connection, String> {
    List<Connection> findByUserId(String userId);
    List<Connection> findByUserIdAndStatus(String userId, ConnectionStatus status);
    Optional<Connection> findByRequesterAndReceiver(String requesterId, String receiverId);
    List<Connection> findPendingRequestsForUser(String userId);
    boolean areConnected(String userId1, String userId2);
}



