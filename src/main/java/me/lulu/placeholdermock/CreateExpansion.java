package me.lulu.placeholdermock;

import java.util.ArrayList;
import java.util.List;

public class CreateExpansion {
    private final PlaceholderMockPlugin plugin;

    public CreateExpansion(PlaceholderMockPlugin plugin) {
        this.plugin = plugin;
    }

    public MockPlaceholderExpansion create(String identifier, List<MockValue> mockValues) {
        return new MockPlaceholderExpansion(this.plugin, identifier, new ArrayList<>(mockValues));
    }
}
