package onlineshopping.services.impl;

import onlineshopping.exceptions.CartException;
import onlineshopping.exceptions.ProductException;
import onlineshopping.models.Cart;
import onlineshopping.models.CartItem;
import onlineshopping.models.Product;
import onlineshopping.repositories.impl.InMemoryCartRepository;
import onlineshopping.repositories.impl.InMemoryProductRepository;
import onlineshopping.services.CartService;
import onlineshopping.services.InventoryService;

import java.util.List;

/**
 * Implementation of CartService
 */
public class CartServiceImpl implements CartService {
    
    private final InMemoryCartRepository cartRepository;
    private final InMemoryProductRepository productRepository;
    private final InventoryService inventoryService;

    public CartServiceImpl(InMemoryCartRepository cartRepository,
                           InMemoryProductRepository productRepository,
                           InventoryService inventoryService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    public Cart getCart(String userId) {
        return cartRepository.getOrCreate(userId);
    }

    @Override
    public void addToCart(String userId, Product product, int quantity) {
        if (!product.isAvailable()) {
            throw ProductException.notAvailable(product.getId());
        }
        
        // Check inventory
        if (!inventoryService.checkAvailability(product.getId(), quantity)) {
            throw CartException.invalidQuantity(quantity);
        }
        
        Cart cart = cartRepository.getOrCreate(userId);
        cart.addItem(product, quantity);
        cartRepository.save(cart);
    }

    @Override
    public void updateQuantity(String userId, String productId, int newQuantity) {
        Cart cart = cartRepository.findById(userId)
            .orElseThrow(() -> CartException.notFound(userId));
        
        if (newQuantity > 0) {
            // Check inventory for the new quantity
            if (!inventoryService.checkAvailability(productId, newQuantity)) {
                throw CartException.invalidQuantity(newQuantity);
            }
        }
        
        cart.updateItemQuantity(productId, newQuantity);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(String userId, String productId) {
        Cart cart = cartRepository.findById(userId)
            .orElseThrow(() -> CartException.notFound(userId));
        
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String userId) {
        cartRepository.clearCart(userId);
    }

    @Override
    public List<CartItem> getCartItems(String userId) {
        return cartRepository.getOrCreate(userId).getItems();
    }

    @Override
    public int getCartItemCount(String userId) {
        return cartRepository.getOrCreate(userId).getItemCount();
    }

    @Override
    public boolean validateCart(String userId) {
        Cart cart = cartRepository.getOrCreate(userId);
        
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            
            if (product == null || !product.isAvailable()) {
                return false;
            }
            
            if (!inventoryService.checkAvailability(item.getProductId(), item.getQuantity())) {
                return false;
            }
        }
        
        return !cart.isEmpty();
    }
}



