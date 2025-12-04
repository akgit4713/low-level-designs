package onlineshopping.exceptions;

/**
 * Exception for cart-related errors
 */
public class CartException extends ShoppingException {

    public CartException(String message) {
        super(message);
    }

    public static CartException notFound(String userId) {
        return new CartException("Cart not found for user: " + userId);
    }

    public static CartException itemNotFound(String productId) {
        return new CartException("Item not found in cart: " + productId);
    }

    public static CartException emptyCart() {
        return new CartException("Cart is empty");
    }

    public static CartException invalidQuantity(int quantity) {
        return new CartException("Invalid quantity: " + quantity);
    }

    public static CartException maxQuantityExceeded(String productId, int maxAllowed) {
        return new CartException(
            String.format("Maximum quantity exceeded for %s. Max allowed: %d", productId, maxAllowed)
        );
    }
}



