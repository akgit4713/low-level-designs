package digitalwallet.repositories;

import digitalwallet.enums.PaymentMethodType;
import digitalwallet.models.PaymentMethod;
import java.util.List;

/**
 * Repository interface for PaymentMethod entity.
 */
public interface PaymentMethodRepository extends Repository<PaymentMethod, String> {
    
    /**
     * Find payment methods by user ID
     */
    List<PaymentMethod> findByUserId(String userId);
    
    /**
     * Find active payment methods by user ID
     */
    List<PaymentMethod> findActiveByUserId(String userId);
    
    /**
     * Find payment methods by user ID and type
     */
    List<PaymentMethod> findByUserIdAndType(String userId, PaymentMethodType type);
    
    /**
     * Count payment methods for a user
     */
    long countByUserId(String userId);
}



