package parkinglot.observers;

import parkinglot.models.ParkingTicket;

/**
 * Observer that sends notifications for parking events.
 * Could be extended to send SMS, email, push notifications.
 */
public class NotificationObserver implements ParkingObserver {
    
    @Override
    public void onVehicleParked(ParkingTicket ticket) {
        // In real implementation, send push notification
        System.out.println("  🔔 Notification: Vehicle " + ticket.getVehicle().getLicensePlate() + 
            " parked successfully. Ticket: " + ticket.getTicketId());
    }

    @Override
    public void onVehicleUnparked(ParkingTicket ticket) {
        // In real implementation, send push notification
        System.out.println("  🔔 Notification: Vehicle " + ticket.getVehicle().getLicensePlate() + 
            " has exited. Thank you for parking!");
    }
}



