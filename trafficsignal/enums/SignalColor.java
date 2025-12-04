package trafficsignal.enums;

/**
 * Represents the color states of a traffic signal.
 */
public enum SignalColor {
    RED("Stop", 0),
    YELLOW("Caution", 1),
    GREEN("Go", 2);

    private final String meaning;
    private final int priority;

    SignalColor(String meaning, int priority) {
        this.meaning = meaning;
        this.priority = priority;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getPriority() {
        return priority;
    }
}



