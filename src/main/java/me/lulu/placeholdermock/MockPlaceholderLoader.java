package me.lulu.placeholdermock;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MockPlaceholderLoader {

    private final CreateExpansion createExpansion;
    private final MockPlaceholdersCache cache;
    private final Supplier<FileConfiguration> configSupplier;
    private final Logger logger;

    public MockPlaceholderLoader(CreateExpansion createExpansion, MockPlaceholdersCache cache, Supplier<FileConfiguration> fileConfiguration, Logger logger) {
        this.createExpansion = createExpansion;
        this.cache = cache;
        this.configSupplier = fileConfiguration;
        this.logger = logger;
    }

    public void loadAndRegisterExpansions() {
        try {
            ConfigurationSection mockSection = this.configSupplier.get().getConfigurationSection("mock_placeholders");
            if (mockSection == null) {
                this.logger.warning("'mock_placeholders' section not found in configuration!");
                return;
            }

            Map<String, Map<String, String>> identifierGroups = parseIdentifierGroups(mockSection);

            for (Map.Entry<String, Map<String, String>> entry : identifierGroups.entrySet()) {
                String identifier = entry.getKey();

                List<MockValue> mockValues = entry.getValue()
                        .entrySet()
                        .stream()
                        .map(e -> new MockValue(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());

                MockPlaceholderExpansion expansion = this.createExpansion.create(identifier, mockValues);

                if (expansion.register()) {
                    this.cache.addExpansion(expansion);
                    this.logger.info("Successfully registered expansion '" + identifier + "' with " + expansion.getMockValuesCount() + " placeholders.");

                    if (this.configSupplier.get().getBoolean("debug", false)) {
                        this.logger.info("  Expansion '" + identifier + "' placeholders:");
                        List<MockValue> mockValueList = expansion.getMockValues();
                        for (MockValue mockValue : mockValueList) {
                            String displayPattern = mockValue.getProcessedPattern();
                            if (mockValue.isDynamic()) {
                                displayPattern += " (dynamic)";
                            }
                            this.logger.info("    %" + identifier + "_" + displayPattern + "% -> " + mockValue.getValue());
                        }
                    }
                } else {
                    this.logger.severe("Failed to register expansion '" + identifier + "'!");
                }
            }

            this.logger.info("Total registered " + this.cache.count() + " PlaceholderAPI expansions.");

        } catch (Exception e) {
            this.logger.log(Level.SEVERE, "Error loading and registering expansions", e);
        }
    }

    public void unregisterAllExpansions() {
        for (MockPlaceholderExpansion expansion : this.cache.getExpansions()) {
            if (expansion.isRegistered()) {
                expansion.unregister();
            }
        }
        this.cache.clear();
    }


    private Map<String, Map<String, String>> parseIdentifierGroups(ConfigurationSection mockSection) {
        Map<String, Map<String, String>> identifierGroups = new HashMap<>();

        Set<String> keys = mockSection.getKeys(false);
        for (String fullKey : keys) {
            String value = mockSection.getString(fullKey);
            if (value == null) {
                continue;
            }

            String[] parts = fullKey.split("_", 2);
            if (parts.length < 2) {
                this.logger.warning("Skipping invalid placeholder key: " + fullKey + " (format should be identifier_placeholder_name)");
                continue;
            }

            String identifier = parts[0];
            String placeholderName = parts[1];

            identifierGroups.computeIfAbsent(identifier, k -> new HashMap<>()).put(placeholderName, value);
        }

        return identifierGroups;
    }
}
