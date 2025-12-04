package bookmyshow.observers;

import bookmyshow.models.Booking;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Observer that tracks booking analytics.
 */
public class AnalyticsObserver implements BookingObserver {
    
    private final AtomicInteger totalBookings = new AtomicInteger(0);
    private final AtomicInteger cancelledBookings = new AtomicInteger(0);
    private final AtomicInteger expiredBookings = new AtomicInteger(0);
    private final AtomicLong totalRevenue = new AtomicLong(0);

    @Override
    public void onBookingConfirmed(Booking booking) {
        totalBookings.incrementAndGet();
        totalRevenue.addAndGet(booking.getTotalAmount().longValue());
        
        System.out.println("\n📊 ANALYTICS UPDATE");
        System.out.println("New booking confirmed. Total bookings: " + totalBookings.get());
        System.out.println("Total revenue: ₹" + totalRevenue.get());
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        cancelledBookings.incrementAndGet();
        
        System.out.println("\n📊 ANALYTICS UPDATE");
        System.out.println("Booking cancelled. Cancellation count: " + cancelledBookings.get());
    }

    @Override
    public void onBookingExpired(Booking booking) {
        expiredBookings.incrementAndGet();
        
        System.out.println("\n📊 ANALYTICS UPDATE");
        System.out.println("Booking expired. Expired count: " + expiredBookings.get());
    }

    // Getters for analytics
    public int getTotalBookings() { return totalBookings.get(); }
    public int getCancelledBookings() { return cancelledBookings.get(); }
    public int getExpiredBookings() { return expiredBookings.get(); }
    public long getTotalRevenue() { return totalRevenue.get(); }

    public double getCancellationRate() {
        int total = totalBookings.get() + cancelledBookings.get();
        return total > 0 ? (double) cancelledBookings.get() / total * 100 : 0;
    }

    public void printSummary() {
        System.out.println("\n=== ANALYTICS SUMMARY ===");
        System.out.println("Total Confirmed Bookings: " + totalBookings.get());
        System.out.println("Cancelled Bookings: " + cancelledBookings.get());
        System.out.println("Expired Bookings: " + expiredBookings.get());
        System.out.println("Total Revenue: ₹" + totalRevenue.get());
        System.out.println("Cancellation Rate: " + String.format("%.2f", getCancellationRate()) + "%");
        System.out.println("========================\n");
    }
}



