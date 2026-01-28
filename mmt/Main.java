// java
package mmt;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.UUID;

public class Main {

    /*
     * Simple in-memory JSON store:
     * - save(String data) parses a flat JSON object (string keys and primitive/string values)
     *   assigns a UID and indexes each value -> list of UIDs that contain that value.
     * - fetch(String attributeRegex) treats the argument as a regex and returns all stored
     *   JSON strings that have at least one value matching the regex.
     */

    interface MemoryTool {
        boolean save(String data);
        List<Object> fetch(String attributeRegex);
    }

    static class MemoryToolImpl implements MemoryTool {

        private final Map<String, String> map = new HashMap<>(); // uid -> raw json string
        private final Map<String, List<String>> pointer = new HashMap<>(); // value -> list of uids

        @Override
        public boolean save(String data) {
            if (data == null) return false;
            String trimmed = data.trim();
            if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return false;

            Map<String, String> parsed = parseFlatJsonToMap(trimmed);
            if (parsed.isEmpty()) {
                // allow storing empty object but still index nothing
                String uidEmpty = UUID.randomUUID().toString();
                map.put(uidEmpty, trimmed);
                return true;
            }

            String uid = UUID.randomUUID().toString();
            map.put(uid, trimmed);

            for (String value : parsed.values()) {
                // index by string representation of the value
                pointer.computeIfAbsent(value, k -> new ArrayList<>()).add(uid);
            }

            return true;
        }

        @Override
        public List<Object> fetch(String attributeRegex) {
            if (attributeRegex == null) return Collections.emptyList();
            Pattern p;
            try {
                p = Pattern.compile(attributeRegex);
            } catch (Exception e) {
                // invalid regex
                return Collections.emptyList();
            }

            // collect unique UIDs matching any indexed value that matches the regex
            LinkedHashSet<String> matchedUids = new LinkedHashSet<>();
            for (Map.Entry<String, List<String>> entry : pointer.entrySet()) {
                String indexedValue = entry.getKey();
                if (p.matcher(indexedValue).find()) {
                    matchedUids.addAll(entry.getValue());
                }
            }

            // return the raw JSON strings for matched UIDs
            return matchedUids.stream()
                    .map(map::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // Very small parser for flat JSON objects like:
        // {"error":"e1","code":503,"flag":true,"name":"abc"}
        // It does not handle nested objects or arrays.
        private Map<String, String> parseFlatJsonToMap(String json) {
            Map<String, String> result = new LinkedHashMap<>();
            // regex captures "key" : "value"  OR  "key" : unquotedValue
            Pattern entry = Pattern.compile("\"(.*?)\"\\s*:\\s*(\"(.*?)\"|[^,}\\s]+)");
            Matcher m = entry.matcher(json);
            while (m.find()) {
                String key = m.group(1);
                String value;
                if (m.group(3) != null) {
                    value = m.group(3);
                } else {
                    value = m.group(2);
                }
                result.put(key, value);
            }
            return result;
        }
    }

    // Simple demonstration
    public static void main(String[] args) {
        MemoryTool tool = new MemoryToolImpl();

        String j1 = "{ \"error\" : \"e1\", \"code\" : \"503\", \"name\":\"serviceA\" }";
        String j2 = "{ \"error\" : \"e2\", \"code\" : \"200\", \"name\":\"serviceB\" }";
        String j3 = "{ \"error\" : \"timeout\", \"code\" : \"503\", \"name\":\"serviceC\" }";

        tool.save(j1);
        tool.save(j2);
        tool.save(j3);

        // fetch entries where any value matches regex "e\\d" (e followed by digit)
        List<Object> found1 = tool.fetch("e\\d");
        System.out.println("Matches for regex 'e\\d':");
        found1.forEach(System.out::println);

        // fetch entries where any value equals "503"
        List<Object> found2 = tool.fetch("^503$");
        System.out.println("\nMatches for regex '^503$':");
        found2.forEach(System.out::println);

        // fetch entries where any value contains "service"
        List<Object> found3 = tool.fetch("service");
        System.out.println("\nMatches for regex 'service':");
        found3.forEach(System.out::println);
    }
}
