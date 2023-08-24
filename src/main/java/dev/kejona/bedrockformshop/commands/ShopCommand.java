package dev.kejona.bedrockformshop.commands;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.forms.ItemListForm;
import dev.kejona.bedrockformshop.forms.ShopsForm;
import dev.kejona.bedrockformshop.utils.FloodgateUser;
import dev.kejona.bedrockformshop.utils.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ShopCommand implements CommandExecutor {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String @NotNull [] args) {
        if (args.length == 0) {
            handleMainMenu(sender);
        } else {
            handleSubCommands(sender, args);
        }
        return true;
    }

    private void handleMainMenu(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed as a player");
            return;
        }

        if (!FloodgateUser.isFloodgatePlayer(player.getUniqueId())) {
            return;
        }

        ShopsForm mainMenuForm = new ShopsForm();
        mainMenuForm.sendShopsForm(player.getUniqueId());
    }

    private void handleSubCommands(@NotNull CommandSender sender, String @NotNull [] args) {
        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            handleReloadCommand(sender);
        } else if (subCommand.equals("open")) {
            handleOpenCommand(sender, args);
        } else {
            sender.sendMessage("The argument " + args[0] + " is not a valid command. Please use /shop");
        }
    }

    private void handleReloadCommand(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            if (!player.hasPermission(String.valueOf(Permission.RELOAD))) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return;
            }
        }

        BedrockFormShop.getInstance().reloadConfigFiles();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfigMessage()));
    }

    private void handleOpenCommand(CommandSender sender, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be performed as a player");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "No shop name was provided; Invalid command.");
            return;
        }

        String inputShop = args[1]; // Get the input shop name without converting to lowercase

        Set<String> shops = SECTION.getShops();
        String foundShop = null;

        for (String shopName : shops) {
            if (shopName.equalsIgnoreCase(inputShop)) {
                foundShop = shopName;
                break;
            }
        }

        if (foundShop == null) {
            player.sendMessage(ChatColor.RED + "The shop you tried to open does not exist");
            return;
        }

        if (!Permission.OPEN_SHOP.checkShopPermission(player.getUniqueId(), foundShop)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to open this shop.");
            return;
        }

        ItemListForm listForm = new ItemListForm(player.getUniqueId());
        listForm.setShopName(foundShop); // Use the found shop name, not the input
        listForm.sendItemListForm();
    }


    private String getConfigMessage() {
        return Objects.requireNonNull(BedrockFormShop.getInstance().getConfig().getString("messages.reload-config"));
    }
}