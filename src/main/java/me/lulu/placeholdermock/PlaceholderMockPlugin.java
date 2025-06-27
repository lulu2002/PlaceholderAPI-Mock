package me.lulu.placeholdermock;

import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderMockPlugin extends JavaPlugin {

    private MockPlaceholderLoader loader;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        MockPlaceholdersCache cache = new MockPlaceholdersCache();
        CreateExpansion createExpansion = new CreateExpansion(this);
        this.loader = new MockPlaceholderLoader(createExpansion, cache, this::getConfig, getLogger());
        PlaceholderMockCommand command = new PlaceholderMockCommand(this::reloadConfig, this.loader, cache, getLogger());

        getCommand("papimock").setExecutor(command);

        if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("PlaceholderAPI not found! Plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.loader.loadAndRegisterExpansions();

        getLogger().info("PlaceholderAPI Mock plugin enabled!");
    }

    @Override
    public void onDisable() {
        this.loader.unregisterAllExpansions();

        getLogger().info("PlaceholderAPI Mock plugin disabled!");
    }


}