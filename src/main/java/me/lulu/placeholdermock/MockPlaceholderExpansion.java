package me.lulu.placeholdermock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockPlaceholderExpansion extends PlaceholderExpansion {

    private final PlaceholderMockPlugin plugin;
    private final String identifier;
    private final List<MockValue> mockValues;

    public MockPlaceholderExpansion(PlaceholderMockPlugin plugin, String identifier, List<MockValue> mockValuesMap) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.mockValues = new ArrayList<>(mockValuesMap);
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        for (MockValue mockValue : mockValues) {
            if (mockValue.matches(params)) {
                return mockValue.process(params, player);
            }
        }

        return null;
    }


    public int getMockValuesCount() {
        return mockValues.size();
    }

    public List<MockValue> getMockValues() {
        return new ArrayList<>(mockValues);
    }
}