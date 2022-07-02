package dev.kejona.bedrockformshop.commands;

import dev.kejona.bedrockformshop.forms.MainMenuForm;
import dev.kejona.bedrockformshop.utils.FloodgateUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (FloodgateUser.isFloodgatePlayer(player.getUniqueId())) {
            MainMenuForm.mainMenu(player.getUniqueId());
            return true;
        }
        return true;
    }
}
