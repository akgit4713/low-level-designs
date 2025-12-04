package linkedin.repositories.impl;

import linkedin.enums.ConnectionStatus;
import linkedin.models.Connection;
import linkedin.repositories.ConnectionRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryConnectionRepository implements ConnectionRepository {
    
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    
    @Override
    public Connection save(Connection connection) {
        connections.put(connection.getId(), connection);
        return connection;
    }
    
    @Override
    public Optional<Connection> findById(String id) {
        return Optional.ofNullable(connections.get(id));
    }
    
    @Override
    public List<Connection> findAll() {
        return new ArrayList<>(connections.values());
    }
    
    @Override
    public void delete(String id) {
        connections.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return connections.containsKey(id);
    }
    
    @Override
    public List<Connection> findByUserId(String userId) {
        return connections.values().stream()
                .filter(c -> c.involvesUser(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Connection> findByUserIdAndStatus(String userId, ConnectionStatus status) {
        return connections.values().stream()
                .filter(c -> c.involvesUser(userId) && c.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Connection> findByRequesterAndReceiver(String requesterId, String receiverId) {
        return connections.values().stream()
                .filter(c -> (c.getRequesterId().equals(requesterId) && c.getReceiverId().equals(receiverId)) ||
                             (c.getRequesterId().equals(receiverId) && c.getReceiverId().equals(requesterId)))
                .findFirst();
    }
    
    @Override
    public List<Connection> findPendingRequestsForUser(String userId) {
        return connections.values().stream()
                .filter(c -> c.getReceiverId().equals(userId) && c.getStatus() == ConnectionStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean areConnected(String userId1, String userId2) {
        return connections.values().stream()
                .anyMatch(c -> c.involvesUser(userId1) && c.involvesUser(userId2) && 
                          c.getStatus() == ConnectionStatus.ACCEPTED);
    }
}



