package fooddelivery.services.impl;

import fooddelivery.enums.AgentStatus;
import fooddelivery.enums.DeliveryStatus;
import fooddelivery.enums.NotificationType;
import fooddelivery.enums.UserRole;
import fooddelivery.exceptions.DeliveryException;
import fooddelivery.models.*;
import fooddelivery.observers.OrderEvent;
import fooddelivery.observers.OrderEventPublisher;
import fooddelivery.repositories.DeliveryAgentRepository;
import fooddelivery.repositories.DeliveryRepository;
import fooddelivery.repositories.OrderRepository;
import fooddelivery.services.DeliveryService;
import fooddelivery.strategies.delivery.DeliveryAssignmentStrategy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of DeliveryService.
 */
public class DeliveryServiceImpl implements DeliveryService {
    
    private final DeliveryAgentRepository agentRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryAssignmentStrategy assignmentStrategy;
    private final OrderEventPublisher eventPublisher;
    
    public DeliveryServiceImpl(DeliveryAgentRepository agentRepository,
                                DeliveryRepository deliveryRepository,
                                OrderRepository orderRepository,
                                DeliveryAssignmentStrategy assignmentStrategy,
                                OrderEventPublisher eventPublisher) {
        this.agentRepository = agentRepository;
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.assignmentStrategy = assignmentStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public DeliveryAgent registerAgent(String name, String email, String phone, String vehicleNumber) {
        String id = "AGENT-" + UUID.randomUUID().toString().substring(0, 8);
        DeliveryAgent agent = new DeliveryAgent(id, name, email, phone, vehicleNumber);
        
        return agentRepository.save(agent);
    }

    @Override
    public Optional<DeliveryAgent> getAgentById(String agentId) {
        return agentRepository.findById(agentId);
    }

    @Override
    public List<DeliveryAgent> getAvailableAgents() {
        return agentRepository.findAvailable();
    }

    @Override
    public void updateAgentStatus(String agentId, AgentStatus status) {
        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new DeliveryException("Agent not found: " + agentId));
        
        agent.setStatus(status);
        agentRepository.save(agent);
    }

    @Override
    public void updateAgentLocation(String agentId, Location location) {
        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new DeliveryException("Agent not found: " + agentId));
        
        agent.updateLocation(location);
        agentRepository.save(agent);
        
        // Also update delivery tracking if agent has active order
        if (agent.getCurrentOrderId() != null) {
            deliveryRepository.findByOrderId(agent.getCurrentOrderId())
                    .ifPresent(delivery -> {
                        delivery.addLocationUpdate(location);
                        deliveryRepository.save(delivery);
                    });
        }
    }

    @Override
    public Delivery createDelivery(Order order, Location restaurantLocation) {
        String deliveryId = "DEL-" + UUID.randomUUID().toString().substring(0, 8);
        Delivery delivery = new Delivery(deliveryId, order.getId(), 
                                         restaurantLocation, order.getDeliveryAddress());
        
        return deliveryRepository.save(delivery);
    }

    @Override
    public boolean assignDeliveryAgent(String deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryException("Delivery not found: " + deliveryId));
        
        if (delivery.getStatus() != DeliveryStatus.PENDING_ASSIGNMENT) {
            return false; // Already assigned
        }
        
        List<DeliveryAgent> availableAgents = agentRepository.findAvailable();
        Optional<DeliveryAgent> selectedAgent = assignmentStrategy.assignAgent(
                availableAgents, delivery.getPickupLocation());
        
        if (selectedAgent.isEmpty()) {
            System.out.println("[DeliveryService] No available agents found for delivery: " + deliveryId);
            return false;
        }
        
        DeliveryAgent agent = selectedAgent.get();
        delivery.assignAgent(agent.getId());
        agent.assignOrder(delivery.getOrderId());
        
        deliveryRepository.save(delivery);
        agentRepository.save(agent);
        
        // Notify agent
        Order order = orderRepository.findById(delivery.getOrderId()).orElse(null);
        if (order != null) {
            eventPublisher.notifyObservers(new OrderEvent(
                NotificationType.DELIVERY_ASSIGNED, order, 
                "Assigned to agent: " + agent.getId()));
        }
        
        System.out.println("[DeliveryService] Assigned agent " + agent.getId() + 
                          " to delivery " + deliveryId);
        return true;
    }

    @Override
    public void pickupOrder(String deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryException("Delivery not found: " + deliveryId));
        
        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new DeliveryException("Cannot pickup - invalid status: " + delivery.getStatus());
        }
        
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        deliveryRepository.save(delivery);
    }

    @Override
    public void completeDelivery(String deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryException("Delivery not found: " + deliveryId));
        
        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);
        
        // Mark agent as available
        if (delivery.getAgentId() != null) {
            agentRepository.findById(delivery.getAgentId())
                    .ifPresent(agent -> {
                        agent.completeDelivery();
                        agentRepository.save(agent);
                    });
        }
    }

    @Override
    public void updateDeliveryLocation(String deliveryId, Location location) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryException("Delivery not found: " + deliveryId));
        
        delivery.addLocationUpdate(location);
        delivery.setStatus(DeliveryStatus.IN_TRANSIT);
        deliveryRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<Delivery> trackDelivery(String deliveryId) {
        return deliveryRepository.findById(deliveryId);
    }

    @Override
    public void rateAgent(String agentId, double rating) {
        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new DeliveryException("Agent not found: " + agentId));
        
        agent.updateRating(rating);
        agentRepository.save(agent);
    }
}



