package dev.kejona.bedrockformshop.commands;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.forms.ShopsForm;
import dev.kejona.bedrockformshop.utils.FloodgateUser;
import dev.kejona.bedrockformshop.utils.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShopCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String @NotNull [] args) {
        // Check if sender is a player & floodgate player.
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (FloodgateUser.isFloodgatePlayer(player.getUniqueId())) {
                    ShopsForm mainMenuForm = new ShopsForm();
                    mainMenuForm.sendShopsForm(player.getUniqueId());
                    return true;
                }
            }
            // If command is /shop reload, reload config.
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player player) {
                if (!Permission.RELOAD.checkPermission(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
            }
            BedrockFormShop.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(BedrockFormShop.getInstance().getConfig().getString("messages.reload-config"))));
        } else {
            sender.sendMessage("The argument " + args[0] + " is not a valid command. please use /shop");
        }
        return true;
    }
}