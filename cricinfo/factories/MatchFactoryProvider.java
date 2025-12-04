package cricinfo.factories;

import cricinfo.enums.MatchFormat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Provider for match factories based on format.
 * Uses Factory pattern to provide appropriate factory for each match format.
 */
public class MatchFactoryProvider {
    
    private static final Map<MatchFormat, MatchFactory> factories = new EnumMap<>(MatchFormat.class);
    
    static {
        factories.put(MatchFormat.T20, new T20MatchFactory());
        factories.put(MatchFormat.ODI, new ODIMatchFactory());
        factories.put(MatchFormat.TEST, new TestMatchFactory());
        factories.put(MatchFormat.T10, new T20MatchFactory()); // Reuse T20 for T10
        factories.put(MatchFormat.HUNDRED, new T20MatchFactory()); // Reuse for Hundred
        factories.put(MatchFormat.FIRST_CLASS, new TestMatchFactory()); // Reuse Test
        factories.put(MatchFormat.LIST_A, new ODIMatchFactory()); // Reuse ODI
    }
    
    /**
     * Get factory for a specific match format.
     */
    public static MatchFactory getFactory(MatchFormat format) {
        MatchFactory factory = factories.get(format);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for format: " + format);
        }
        return factory;
    }
    
    /**
     * Register a custom factory for a format.
     */
    public static void registerFactory(MatchFormat format, MatchFactory factory) {
        factories.put(format, factory);
    }
    
    /**
     * Check if a factory exists for the format.
     */
    public static boolean hasFactory(MatchFormat format) {
        return factories.containsKey(format);
    }
}



