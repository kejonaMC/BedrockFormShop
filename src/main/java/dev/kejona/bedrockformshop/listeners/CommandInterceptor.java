package dev.kejona.bedrockformshop.listeners;

import dev.kejona.bedrockformshop.forms.ShopsForm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandInterceptor implements Listener {
    private final List<String> commands;
    public CommandInterceptor(List<String> commands) {
        this.commands = commands;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreProcessCommand(@NotNull PlayerCommandPreprocessEvent event) {
        // Check if command is in list.
        if (commands.contains(event.getMessage().split(" ")[0])) {
            // Command is in list so cancel event and send our shop form.
            event.setCancelled(true);
            ShopsForm mainMenuForm = new ShopsForm();
            mainMenuForm.sendShopsForm(event.getPlayer().getUniqueId());
        }
    }
}
