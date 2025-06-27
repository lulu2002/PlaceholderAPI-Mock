package me.lulu.placeholdermock;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockValue {

    private final String pattern;
    private final String value;
    private final Pattern compiledPattern;
    private final boolean isDynamic;

    public MockValue(String pattern, String value) {
        this.pattern = pattern;
        this.value = value;
        this.isDynamic = pattern.contains("<") && pattern.contains(">");

        if (isDynamic) {
            String regexPattern = pattern.replaceAll("<[^>]+>", "(.+)");
            this.compiledPattern = Pattern.compile("^" + regexPattern + "$");
        } else {
            this.compiledPattern = null;
        }
    }

    /**
     * Check if the given placeholder matches this mock value's pattern
     */
    public boolean matches(String placeholder) {
        if (!isDynamic) {
            return pattern.equals(placeholder);
        } else {
            return compiledPattern.matcher(placeholder).matches();
        }
    }

    /**
     * Process the placeholder and return the corresponding value
     */
    public String process(String placeholder, OfflinePlayer player) {
        String processedValue = value;

        if (isDynamic) {
            Matcher matcher = compiledPattern.matcher(placeholder);
            if (matcher.matches()) {
                Map<String, String> extractedValues = extractValues(matcher);

                for (Map.Entry<String, String> entry : extractedValues.entrySet()) {
                    processedValue = processedValue.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
        }

        return processedValue;
    }

    /**
     * Extract values from the matcher and map them to variable names
     */
    private Map<String, String> extractValues(Matcher matcher) {
        Map<String, String> extractedValues = new HashMap<>();

        Pattern variablePattern = Pattern.compile("<([^>]+)>");
        Matcher variableMatcher = variablePattern.matcher(pattern);

        int groupIndex = 1;
        while (variableMatcher.find() && groupIndex <= matcher.groupCount()) {
            String variableName = variableMatcher.group(1);
            String extractedValue = matcher.group(groupIndex);
            extractedValues.put(variableName, extractedValue);
            groupIndex++;
        }

        return extractedValues;
    }

    public String getPattern() {
        return pattern;
    }

    public String getValue() {
        return value;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    @Override
    public String toString() {
        return "MockValue{" +
                "pattern='" + pattern + '\'' +
                ", value='" + value + '\'' +
                ", isDynamic=" + isDynamic +
                '}';
    }
} 