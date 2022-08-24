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
import java.math.BigDecimal;
import java.util.*;

public class TransactionHandler extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final UUID uuid;
    private final Material item;
    private final BigDecimal buyPrice;
    private final BigDecimal sellPrice;
    private final int quantity;
    Logger logger = Logger.getLogger();

    public TransactionHandler(UUID uuid, Material item, BigDecimal buyPrice, BigDecimal sellPrice, int quantity) {
        this.uuid = uuid;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }

    public void buyItem() {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        assert player != null;
        // Check if player has enough money or if the shop is disabled.
        // If price is null then item is not buy-able.
        if (buyPrice == null) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("no-buy-price")));
            return;
        }
        // Check if player has enough balance.
        if (VaultAPI.eco().getBalance(player).compareTo(buyPrice.multiply(BigDecimal.valueOf(quantity))) < 0) {
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
            return;
        }
        // Setup items and inventory.
        ItemInventorySetup itemInventorySetup = new ItemInventorySetup(
                item,
                SECTION.getButtonData(getMenuID(), getButtonID()).getString("type"),
                player,
                quantity
        );
        itemInventorySetup.setMenuID(getMenuID());
        itemInventorySetup.setButtonID(getButtonID());
        // If success = item has been succesfully created and sent to player inventory.
        if (itemInventorySetup.buyItemSuccess()) {
            // Withdraw money from player.
            VaultAPI.eco().withdrawBalance(player, buyPrice.multiply(BigDecimal.valueOf(quantity)));
            // Print the transaction to txt file.
            try {
                new FileWriterShopOutput(player.getName(), buyPrice, quantity, item.name());
            } catch (IOException e) {
                logger.severe("Could not save shop data." + e.getMessage());
            }
            player.sendMessage(Placeholders.set(SECTION.getMessages("item-bought"), itemInventorySetup.getItemName(), buyPrice, quantity));
        }
    }

    public void sellItem() {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if shop is disabled and is not set on 0
        if (sellPrice == null ||sellPrice.doubleValue() == 0.0 || sellPrice.intValue() == 0) {
            assert player != null;
            player.sendMessage(Placeholders.colorCode(SECTION.getMessages("no-sell-price")));
            return;
        }
        // Get an itemstack list from all items in player inventory.
        List<ItemStack> list = new ArrayList<>();
        assert player != null;
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
            if (fullinventory.getType() == item && fullinventory.getAmount() >= quantity) {
                // Withdraw money from player
                VaultAPI.eco().depositBalance(player, sellPrice.multiply(BigDecimal.valueOf(quantity)));
                // Remove item from player.
                fullinventory.setAmount(fullinventory.getAmount() - quantity);
                // Print the transaction to txt file
                try {
                    new FileWriterShopOutput(player.getName(), sellPrice, quantity, item.name());
                } catch (IOException e) {
                    logger.severe("Could not save shop data." + e.getMessage());
                }
                player.sendMessage(Placeholders.set(SECTION.getMessages("item-sold"), item.name(), sellPrice, quantity));
                return;
                // Player does not have the right amount of items.
            } else if (fullinventory.getType() == item && fullinventory.getAmount() < quantity) {
                player.sendMessage(Placeholders.set(SECTION.getMessages("not-enough-items"), item.name()));
                return;
            }
        }
    }
}