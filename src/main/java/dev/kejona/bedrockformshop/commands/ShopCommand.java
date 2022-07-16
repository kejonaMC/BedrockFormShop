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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Check if player used shop command and is a floodgate user.
            if (args.length == 0 && FloodgateUser.isFloodgatePlayer(player.getUniqueId())) {
                MainMenuForm mainMenuForm = new MainMenuForm();
                mainMenuForm.mainMenu(player.getUniqueId());
                return true;
            }
        } else {
            // Sender is probably console. and can't open shops ofc
            sender.sendMessage(ChatColor.RED + "You cannot preform command. This command only works for floodgate players");
            return true;
        }
        // If Arg is reload then reload config, can also be used in console.
        if (args[0].equalsIgnoreCase("reload")) {
            // If arg was reload, reload config
            BedrockFormShop.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(BedrockFormShop.getInstance().getConfig().getString("messages.reload-config"))));
            return true;
        } else {
            sender.sendMessage("The argument " + args[0] + " is not a valid command. please use /shop");
        }

        return false;
    }
}