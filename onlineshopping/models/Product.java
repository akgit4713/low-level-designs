package onlineshopping.models;

import onlineshopping.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a product in the catalog
 * Uses Builder pattern for construction
 */
public class Product {
    private final String id;
    private final String sellerId;
    private final String name;
    private final String description;
    private final Category category;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;
    private final Map<String, String> attributes; // Custom attributes like size, color, etc.

    private BigDecimal price;
    private BigDecimal originalPrice; // For showing discounts
    private ProductStatus status;
    private double averageRating;
    private int reviewCount;

    private Product(Builder builder) {
        this.id = builder.id;
        this.sellerId = builder.sellerId;
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.price = builder.price;
        this.originalPrice = builder.originalPrice != null ? builder.originalPrice : builder.price;
        this.status = builder.status;
        this.imageUrls = new ArrayList<>(builder.imageUrls);
        this.attributes = new HashMap<>(builder.attributes);
        this.createdAt = LocalDateTime.now();
        this.averageRating = 0.0;
        this.reviewCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = Objects.requireNonNull(price);
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<String> getImageUrls() {
        return Collections.unmodifiableList(imageUrls);
    }

    public void addImageUrl(String url) {
        imageUrls.add(url);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public Optional<String> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void updateRating(double newRating) {
        double totalRating = averageRating * reviewCount;
        reviewCount++;
        averageRating = (totalRating + newRating) / reviewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAvailable() {
        return status.isAvailable();
    }

    public boolean hasDiscount() {
        return originalPrice.compareTo(price) > 0;
    }

    public BigDecimal getDiscountAmount() {
        return originalPrice.subtract(price);
    }

    public int getDiscountPercentage() {
        if (!hasDiscount()) return 0;
        return originalPrice.subtract(price)
            .multiply(BigDecimal.valueOf(100))
            .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP)
            .intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%s, status=%s}", 
            id, name, price, status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String sellerId;
        private String name;
        private String description = "";
        private Category category;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private ProductStatus status = ProductStatus.ACTIVE;
        private List<String> imageUrls = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder sellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder originalPrice(BigDecimal originalPrice) {
            this.originalPrice = originalPrice;
            return this;
        }

        public Builder status(ProductStatus status) {
            this.status = status;
            return this;
        }

        public Builder imageUrls(List<String> imageUrls) {
            this.imageUrls = new ArrayList<>(imageUrls);
            return this;
        }

        public Builder addImageUrl(String url) {
            this.imageUrls.add(url);
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = new HashMap<>(attributes);
            return this;
        }

        public Builder addAttribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        public Product build() {
            Objects.requireNonNull(id, "Product ID is required");
            Objects.requireNonNull(sellerId, "Seller ID is required");
            Objects.requireNonNull(name, "Product name is required");
            Objects.requireNonNull(category, "Category is required");
            Objects.requireNonNull(price, "Price is required");
            
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            
            return new Product(this);
        }
    }
}



