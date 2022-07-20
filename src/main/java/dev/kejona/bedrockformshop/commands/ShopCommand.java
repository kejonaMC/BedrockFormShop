package dev.kejona.bedrockformshop.commands;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.forms.MainMenuForm;
import dev.kejona.bedrockformshop.utils.FloodgateUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShopCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        // Check if sender is a player & floodgate player.
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (FloodgateUser.isFloodgatePlayer(player.getUniqueId())) {
                    MainMenuForm mainMenuForm = new MainMenuForm();
                    mainMenuForm.mainMenu(player.getUniqueId());
                    return true;
                }
            }
            notFloodgatePlayer(sender);
            // If command is /shop reload, reload config.
        } else if (args[0].equalsIgnoreCase("reload")) {
            BedrockFormShop.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(BedrockFormShop.getInstance().getConfig().getString("messages.reload-config"))));
        } else {
            sender.sendMessage("The argument " + args[0] + " is not a valid command. please use /shop");
        }
        return true;
    }

    private static void notFloodgatePlayer(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You cannot preform command. This command only works for floodgate players");
    }
}