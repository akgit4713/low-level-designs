package librarymanagement.observers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Observer that sends email notifications for library events.
 */
public class EmailNotificationObserver implements LibraryEventObserver {
    
    private static final Set<LibraryEvent.EventType> INTERESTED_TYPES = new HashSet<>(Arrays.asList(
            LibraryEvent.EventType.BOOK_BORROWED,
            LibraryEvent.EventType.BOOK_RETURNED,
            LibraryEvent.EventType.BOOK_OVERDUE,
            LibraryEvent.EventType.FINE_ISSUED
    ));

    @Override
    public void onEvent(LibraryEvent event) {
        // In a real implementation, this would send an actual email
        System.out.println("[EMAIL] Sending notification: " + event.getMessage());
        
        switch (event.getType()) {
            case BOOK_BORROWED:
                sendBorrowConfirmation(event);
                break;
            case BOOK_RETURNED:
                sendReturnConfirmation(event);
                break;
            case BOOK_OVERDUE:
                sendOverdueReminder(event);
                break;
            case FINE_ISSUED:
                sendFineNotice(event);
                break;
            default:
                break;
        }
    }

    private void sendBorrowConfirmation(LibraryEvent event) {
        System.out.println("[EMAIL] Borrow confirmation sent for: " + event.getEntityId());
    }

    private void sendReturnConfirmation(LibraryEvent event) {
        System.out.println("[EMAIL] Return confirmation sent for: " + event.getEntityId());
    }

    private void sendOverdueReminder(LibraryEvent event) {
        System.out.println("[EMAIL] Overdue reminder sent for: " + event.getEntityId());
    }

    private void sendFineNotice(LibraryEvent event) {
        System.out.println("[EMAIL] Fine notice sent for: " + event.getEntityId());
    }

    @Override
    public LibraryEvent.EventType[] getInterestedEventTypes() {
        return INTERESTED_TYPES.toArray(new LibraryEvent.EventType[0]);
    }
}



