package me.lulu.placeholdermock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceholderMockCommand implements CommandExecutor {

    private final Runnable reloadConfigFn;
    private final MockPlaceholderLoader loader;
    private final MockPlaceholdersCache cache;
    private final Logger logger;

    public PlaceholderMockCommand(Runnable reloadConfigFn, MockPlaceholderLoader loader, MockPlaceholdersCache cache, Logger logger) {
        this.reloadConfigFn = reloadConfigFn;
        this.loader = loader;
        this.cache = cache;
        this.logger = logger;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("placeholdermock.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUsage: /phmock <reload>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            try {
                reloadConfigFn.run();

                this.loader.unregisterAllExpansions();
                this.loader.loadAndRegisterExpansions();

                sender.sendMessage("§aConfiguration reloaded!");

                int totalPlaceholders = this.cache.getExpansions()
                        .stream()
                        .reduce(
                                0,
                                (sum, expansion) -> sum + expansion.getMockValuesCount(),
                                Integer::sum
                        );
                sender.sendMessage("§aLoaded " + this.cache.count() + " expansions with " + totalPlaceholders + " mock placeholders.");

            } catch (Exception e) {
                sender.sendMessage("§cError reloading configuration: " + e.getMessage());
                this.logger.log(Level.SEVERE, "Error reloading configuration", e);
            }
            return true;
        }

        sender.sendMessage("§cUnknown subcommand: " + args[0]);
        return true;
    }


}
