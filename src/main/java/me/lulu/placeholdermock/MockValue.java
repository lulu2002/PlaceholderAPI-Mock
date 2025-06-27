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
    private final String processedPattern;

    public MockValue(String pattern, String value) {
        this.pattern = pattern;
        this.value = value;
        
        String tempPattern = pattern.replace("\\<", "___ESCAPED_LT___").replace("\\>", "___ESCAPED_GT___");
        
        this.isDynamic = tempPattern.contains("<") && tempPattern.contains(">");
        
        if (isDynamic) {
            String regexPattern = tempPattern.replaceAll("<[^>]+>", "(.+)");
            regexPattern = regexPattern.replace("___ESCAPED_LT___", "<").replace("___ESCAPED_GT___", ">");
            this.compiledPattern = Pattern.compile("^" + regexPattern + "$");
            this.processedPattern = pattern.replace("\\<", "<").replace("\\>", ">");
        } else {
            this.compiledPattern = null;
            this.processedPattern = pattern.replace("\\<", "<").replace("\\>", ">");
        }
    }

    /**
     * Check if the given placeholder matches this mock value's pattern
     */
    public boolean matches(String placeholder) {
        if (!isDynamic) {
            return processedPattern.equals(placeholder);
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

        processedValue = processedValue.replace("\\<", "<").replace("\\>", ">");

        return processedValue;
    }

    /**
     * Extract values from the matcher and map them to variable names
     */
    private Map<String, String> extractValues(Matcher matcher) {
        Map<String, String> extractedValues = new HashMap<>();

        String tempPattern = pattern.replace("\\<", "___ESCAPED_LT___").replace("\\>", "___ESCAPED_GT___");
        Pattern variablePattern = Pattern.compile("<([^>]+)>");
        Matcher variableMatcher = variablePattern.matcher(tempPattern);

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

    public String getProcessedPattern() {
        return processedPattern;
    }

    @Override
    public String toString() {
        return "MockValue{" +
                "pattern='" + pattern + '\'' +
                ", processedPattern='" + processedPattern + '\'' +
                ", value='" + value + '\'' +
                ", isDynamic=" + isDynamic +
                '}';
    }
} 