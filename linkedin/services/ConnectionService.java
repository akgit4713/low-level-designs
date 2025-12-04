package linkedin.services;

import linkedin.enums.ConnectionStatus;
import linkedin.exceptions.ConnectionException;
import linkedin.exceptions.UserNotFoundException;
import linkedin.factories.NotificationFactory;
import linkedin.models.Connection;
import linkedin.models.Notification;
import linkedin.models.User;
import linkedin.repositories.ConnectionRepository;
import linkedin.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing connections between users.
 */
public class ConnectionService {
    
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    public ConnectionService(ConnectionRepository connectionRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    public Connection sendConnectionRequest(String requesterId, String receiverId) {
        validateUsers(requesterId, receiverId);
        
        if (requesterId.equals(receiverId)) {
            throw new ConnectionException("Cannot send connection request to yourself");
        }
        
        // Check if connection already exists
        Optional<Connection> existingConnection = 
                connectionRepository.findByRequesterAndReceiver(requesterId, receiverId);
        
        if (existingConnection.isPresent()) {
            Connection conn = existingConnection.get();
            switch (conn.getStatus()) {
                case ACCEPTED:
                    throw new ConnectionException("Already connected");
                case PENDING:
                    throw new ConnectionException("Connection request already pending");
                case BLOCKED:
                    throw new ConnectionException("Cannot send connection request");
                case DECLINED:
                    // Allow resending after decline
                    break;
            }
        }
        
        Connection connection = new Connection(requesterId, receiverId);
        connectionRepository.save(connection);
        
        // Send notification
        User requester = userRepository.findById(requesterId).get();
        User receiver = userRepository.findById(receiverId).get();
        Notification notification = NotificationFactory.createConnectionRequestNotification(requester, receiver);
        notificationService.notify(notification);
        
        return connection;
    }
    
    public Connection acceptConnectionRequest(String connectionId, String userId) {
        Connection connection = getConnectionById(connectionId);
        
        // Only the receiver can accept
        if (!connection.getReceiverId().equals(userId)) {
            throw new ConnectionException("Only the receiver can accept the connection request");
        }
        
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new ConnectionException("Connection request is not pending");
        }
        
        connection.accept();
        connectionRepository.save(connection);
        
        // Notify the requester
        User accepter = userRepository.findById(userId).get();
        User requester = userRepository.findById(connection.getRequesterId()).get();
        Notification notification = NotificationFactory.createConnectionAcceptedNotification(accepter, requester);
        notificationService.notify(notification);
        
        return connection;
    }
    
    public Connection declineConnectionRequest(String connectionId, String userId) {
        Connection connection = getConnectionById(connectionId);
        
        if (!connection.getReceiverId().equals(userId)) {
            throw new ConnectionException("Only the receiver can decline the connection request");
        }
        
        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new ConnectionException("Connection request is not pending");
        }
        
        connection.decline();
        connectionRepository.save(connection);
        
        return connection;
    }
    
    public void removeConnection(String userId, String connectionUserId) {
        Optional<Connection> connection = 
                connectionRepository.findByRequesterAndReceiver(userId, connectionUserId);
        
        if (connection.isEmpty() || connection.get().getStatus() != ConnectionStatus.ACCEPTED) {
            throw new ConnectionException("Not connected with this user");
        }
        
        connectionRepository.delete(connection.get().getId());
    }
    
    public void blockUser(String blockerId, String blockedId) {
        validateUsers(blockerId, blockedId);
        
        Optional<Connection> existingConnection = 
                connectionRepository.findByRequesterAndReceiver(blockerId, blockedId);
        
        if (existingConnection.isPresent()) {
            Connection conn = existingConnection.get();
            conn.block();
            connectionRepository.save(conn);
        } else {
            Connection connection = new Connection(blockerId, blockedId);
            connection.block();
            connectionRepository.save(connection);
        }
    }
    
    public List<User> getConnections(String userId) {
        List<Connection> connections = connectionRepository
                .findByUserIdAndStatus(userId, ConnectionStatus.ACCEPTED);
        
        return connections.stream()
                .map(conn -> conn.getOtherUserId(userId))
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }
    
    public List<Connection> getPendingRequests(String userId) {
        return connectionRepository.findPendingRequestsForUser(userId);
    }
    
    public List<Connection> getSentRequests(String userId) {
        return connectionRepository.findByUserId(userId).stream()
                .filter(c -> c.getRequesterId().equals(userId) && 
                            c.getStatus() == ConnectionStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    public boolean areConnected(String userId1, String userId2) {
        return connectionRepository.areConnected(userId1, userId2);
    }
    
    public int getConnectionCount(String userId) {
        return connectionRepository.findByUserIdAndStatus(userId, ConnectionStatus.ACCEPTED).size();
    }
    
    private Connection getConnectionById(String connectionId) {
        return connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ConnectionException("Connection not found: " + connectionId));
    }
    
    private void validateUsers(String userId1, String userId2) {
        if (!userRepository.existsById(userId1)) {
            throw new UserNotFoundException(userId1);
        }
        if (!userRepository.existsById(userId2)) {
            throw new UserNotFoundException(userId2);
        }
    }
}



