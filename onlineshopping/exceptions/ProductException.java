package onlineshopping.exceptions;

/**
 * Exception for product-related errors
 */
public class ProductException extends ShoppingException {

    public ProductException(String message) {
        super(message);
    }

    public static ProductException notFound(String productId) {
        return new ProductException("Product not found: " + productId);
    }

    public static ProductException notAvailable(String productId) {
        return new ProductException("Product not available for purchase: " + productId);
    }

    public static ProductException invalidCategory(String categoryId) {
        return new ProductException("Invalid category: " + categoryId);
    }

    public static ProductException duplicateProduct(String productId) {
        return new ProductException("Product already exists: " + productId);
    }
}



