package dev.kejona.bedrockformshop.listeners;

import dev.kejona.bedrockformshop.forms.ShopsForm;
import dev.kejona.bedrockformshop.utils.FloodgateUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CommandInterceptor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreProcessCommand(@NotNull PlayerCommandPreprocessEvent event) {
        // no reason to check arrays since both java and bedrock players should be able to use reload command.
        if (event.getMessage().equalsIgnoreCase("/shop")) {
            if (FloodgateUser.isFloodgatePlayer(event.getPlayer().getUniqueId())){
                event.setCancelled(true);
                ShopsForm mainMenuForm = new ShopsForm();
                mainMenuForm.sendShopsForm(event.getPlayer().getUniqueId());
            }
        }
    }
}
