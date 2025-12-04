package carrental.models;

import carrental.enums.CarType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Value object representing search criteria for finding available cars.
 * Uses Builder pattern for flexible criteria construction.
 */
public class SearchCriteria {
    private final CarType carType;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String make;
    private final Integer minYear;
    private final Integer minSeats;

    private SearchCriteria(Builder builder) {
        this.carType = builder.carType;
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.make = builder.make;
        this.minYear = builder.minYear;
        this.minSeats = builder.minSeats;
    }

    // Getters
    public CarType getCarType() { return carType; }
    public BigDecimal getMinPrice() { return minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getMake() { return make; }
    public Integer getMinYear() { return minYear; }
    public Integer getMinSeats() { return minSeats; }

    public boolean hasCarType() { return carType != null; }
    public boolean hasPriceRange() { return minPrice != null || maxPrice != null; }
    public boolean hasDateRange() { return startDate != null && endDate != null; }
    public boolean hasMake() { return make != null && !make.isEmpty(); }
    public boolean hasMinYear() { return minYear != null; }
    public boolean hasMinSeats() { return minSeats != null; }

    /**
     * Checks if a car matches this search criteria.
     */
    public boolean matches(Car car) {
        if (hasCarType() && car.getCarType() != carType) {
            return false;
        }
        if (minPrice != null && car.getEffectivePricePerDay().compareTo(minPrice) < 0) {
            return false;
        }
        if (maxPrice != null && car.getEffectivePricePerDay().compareTo(maxPrice) > 0) {
            return false;
        }
        if (hasMake() && !car.getMake().equalsIgnoreCase(make)) {
            return false;
        }
        if (hasMinYear() && car.getYear() < minYear) {
            return false;
        }
        if (hasMinSeats() && car.getSeatCapacity() < minSeats) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SearchCriteria{");
        if (hasCarType()) sb.append("type=").append(carType).append(", ");
        if (minPrice != null) sb.append("minPrice=$").append(minPrice).append(", ");
        if (maxPrice != null) sb.append("maxPrice=$").append(maxPrice).append(", ");
        if (hasDateRange()) sb.append("dates=").append(startDate).append(" to ").append(endDate).append(", ");
        if (hasMake()) sb.append("make=").append(make).append(", ");
        if (hasMinYear()) sb.append("minYear=").append(minYear).append(", ");
        if (hasMinSeats()) sb.append("minSeats=").append(minSeats);
        return sb.append("}").toString();
    }

    // Builder Pattern
    public static class Builder {
        private CarType carType;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private LocalDate startDate;
        private LocalDate endDate;
        private String make;
        private Integer minYear;
        private Integer minSeats;

        public Builder carType(CarType carType) { this.carType = carType; return this; }
        public Builder minPrice(BigDecimal minPrice) { this.minPrice = minPrice; return this; }
        public Builder maxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder dateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }
        public Builder priceRange(BigDecimal minPrice, BigDecimal maxPrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            return this;
        }
        public Builder make(String make) { this.make = make; return this; }
        public Builder minYear(Integer minYear) { this.minYear = minYear; return this; }
        public Builder minSeats(Integer minSeats) { this.minSeats = minSeats; return this; }

        public SearchCriteria build() {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
                throw new IllegalArgumentException("Min price cannot be greater than max price");
            }
            return new SearchCriteria(this);
        }
    }
}



