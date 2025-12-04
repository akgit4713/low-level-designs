package onlineshopping.strategies.search;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Value object for search criteria
 */
public class SearchCriteria {
    private String keyword;
    private String categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private String sellerId;
    private SortBy sortBy;
    private boolean ascending;
    private int page;
    private int pageSize;

    public enum SortBy {
        RELEVANCE,
        PRICE,
        RATING,
        NEWEST,
        POPULARITY
    }

    private SearchCriteria(Builder builder) {
        this.keyword = builder.keyword;
        this.categoryId = builder.categoryId;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.minRating = builder.minRating;
        this.sellerId = builder.sellerId;
        this.sortBy = builder.sortBy;
        this.ascending = builder.ascending;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    public Optional<String> getKeyword() {
        return Optional.ofNullable(keyword);
    }

    public Optional<String> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public Optional<BigDecimal> getMinPrice() {
        return Optional.ofNullable(minPrice);
    }

    public Optional<BigDecimal> getMaxPrice() {
        return Optional.ofNullable(maxPrice);
    }

    public Optional<Double> getMinRating() {
        return Optional.ofNullable(minRating);
    }

    public Optional<String> getSellerId() {
        return Optional.ofNullable(sellerId);
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keyword;
        private String categoryId;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Double minRating;
        private String sellerId;
        private SortBy sortBy = SortBy.RELEVANCE;
        private boolean ascending = true;
        private int page = 0;
        private int pageSize = 20;

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder categoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder minPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder priceRange(BigDecimal min, BigDecimal max) {
            this.minPrice = min;
            this.maxPrice = max;
            return this;
        }

        public Builder minRating(Double minRating) {
            this.minRating = minRating;
            return this;
        }

        public Builder sellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder sortBy(SortBy sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder ascending(boolean ascending) {
            this.ascending = ascending;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
}



