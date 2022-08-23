package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Placeholders;
import dev.kejona.bedrockformshop.shopdata.FileWriterShopOutput;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class TransactionHandler extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final UUID uuid;
    private final Material item;
    private final double buyPrice;
    private final int amount;
    private final double sellPrice;
    Logger logger = Logger.getLogger();

    public TransactionHandler(UUID uuid, Material item, double buyPrice, double sellPrice, int amount) {
        this.uuid = uuid;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
    }

    public void buyItem() {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if player has enough space in inventory.
        assert player != null;
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("inventory-full")));
            return;
        }
        // Check if player has enough money.
        if (VaultAPI.eco().getBalance(player) < buyPrice * amount) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
            return;
        }
        // Create the ItemStack
        ItemBuilder itemBuilder = new ItemBuilder(
                item,
                amount,
                SECTION.getButtonData(getMenuID(), getButtonID()).getString("type")
        );
        // Withdraw money from player.
        VaultAPI.eco().withdrawBalance(player, buyPrice * amount);
        // Give item to player.
        player.getInventory().addItem(itemBuilder.item());

        try {
            new FileWriterShopOutput(player.getName(), buyPrice, amount, itemBuilder.item().getType());
        } catch (IOException e) {
            logger.severe("Could not save shop data." + e.getMessage());
        }

        player.sendMessage(Placeholders.set(SECTION.getMessages("item-bought"), itemBuilder.getItemName(), buyPrice, amount));
    }

    public void sellItem() {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Get Item from form.
        assert player != null;
        // Get an itemstack list from all items in player inventory.
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack i : player.getInventory().getContents()) {
            if (i != null)
                list.add(i);
        }

        ItemStack[] inv = list.toArray(new ItemStack[0]);
        for (ItemStack fullinventory : inv) {
            if (fullinventory == null) {
                player.sendMessage(Placeholders.set(SECTION.getMessages("no-items"), item.name()));
                return;
            }
            // Get item from itemname and has the correct amount of items.
            if (fullinventory.getType() == item && fullinventory.getAmount() >= amount) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, sellPrice * amount);
                // Remove item from player.
                fullinventory.setAmount(fullinventory.getAmount() - amount);
                try {
                    new FileWriterShopOutput(player.getName(), sellPrice, amount, item);
                } catch (IOException e) {
                    logger.severe("Could not save shop data." + e.getMessage());
                }
                player.sendMessage(Placeholders.set(SECTION.getMessages("item-sold"), item.name(), sellPrice, amount));
                return;
                // Player does not have the right amount of items.
            } else if (fullinventory.getType() == item && fullinventory.getAmount() < amount) {
                player.sendMessage(Placeholders.set(SECTION.getMessages("not-enough-items"), item.name()));
                return;
            }
        }
    }
}