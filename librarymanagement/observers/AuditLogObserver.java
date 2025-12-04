package librarymanagement.observers;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Observer that logs all library events for audit purposes.
 */
public class AuditLogObserver implements LibraryEventObserver {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final PrintStream output;
    private final List<LibraryEvent> eventLog;
    private final boolean storeEvents;

    public AuditLogObserver() {
        this(System.out, false);
    }

    public AuditLogObserver(PrintStream output, boolean storeEvents) {
        this.output = output;
        this.storeEvents = storeEvents;
        this.eventLog = storeEvents ? new ArrayList<>() : null;
    }

    @Override
    public void onEvent(LibraryEvent event) {
        String logEntry = formatLogEntry(event);
        output.println(logEntry);
        
        if (storeEvents && eventLog != null) {
            eventLog.add(event);
        }
    }

    private String formatLogEntry(LibraryEvent event) {
        return String.format("[AUDIT] [%s] [%s] Entity: %s - %s",
                event.getTimestamp().format(FORMATTER),
                event.getType(),
                event.getEntityId(),
                event.getMessage());
    }

    public List<LibraryEvent> getEventLog() {
        return eventLog != null ? new ArrayList<>(eventLog) : new ArrayList<>();
    }

    public void clearEventLog() {
        if (eventLog != null) {
            eventLog.clear();
        }
    }
}



