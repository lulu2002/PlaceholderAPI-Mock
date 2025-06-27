package me.lulu.placeholdermock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockPlaceholdersCache {

    private final Map<String, MockPlaceholderExpansion> expansions = new HashMap<>();

    public void addExpansion(MockPlaceholderExpansion expansion) {
        expansions.put(expansion.getIdentifier(), expansion);
    }

    public int count() {
        return expansions.size();
    }

    public List<MockPlaceholderExpansion> getExpansions() {
        return List.copyOf(expansions.values());
    }

    public void clear() {
        expansions.clear();
    }

}
