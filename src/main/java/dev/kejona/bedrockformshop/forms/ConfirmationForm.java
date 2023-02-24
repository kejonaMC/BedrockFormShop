package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.ItemInventorySetup;
import dev.kejona.bedrockformshop.handlers.VaultAPI;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Placeholders;
import dev.kejona.bedrockformshop.shopdata.FileWriterShopOutput;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ConfirmationForm extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final UUID uuid;
    private final Material item;
    private final BigDecimal buyPrice;
    private final BigDecimal sellPrice;
    private final int quantity;
    private final Logger logger = Logger.getLogger();
    private final ModalForm.Builder form;

    public ConfirmationForm(UUID uuid, Material item, BigDecimal buyPrice, BigDecimal sellPrice, int quantity) {
        this.uuid = uuid;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        // Build the form.
        form = ModalForm.builder();
        form.title(Objects.requireNonNull(SECTION.getMenuData("confirmation").getString("title")));
        form.button1(Objects.requireNonNull(SECTION.getMenuData("confirmation").getString("button1")));
        form.button2(Objects.requireNonNull(SECTION.getMenuData("confirmation").getString("button2")));
    }

    /**
     * Buy Item logic.
     */
    public void buyItem() {
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        assert player != null;
        // Check if player has enough money or if the shop is disabled.
        // If price is null then item is not buy-able.
        if (buyPrice == null) {
            form.content(Placeholders.colorCode(SECTION.getMessages("no-buy-price")));
            FloodgateApi.getInstance().sendForm(uuid, form.build());
            return;
        }
        // Check if player has enough balance.
        if (VaultAPI.eco().getBalance(player).compareTo(buyPrice.multiply(BigDecimal.valueOf(quantity))) < 0) {
            form.content(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
            FloodgateApi.getInstance().sendForm(uuid, form.build());
            return;
        }
        // user can buy the items.
        form.content(Objects.requireNonNull(Placeholders.set(SECTION.getMenuData("confirmation").getString("content"), item.name(), buyPrice, quantity)));
        form.validResultHandler(response -> {
            if (response.clickedFirst()) {
                // Setup items and inventory.
                ItemInventorySetup itemInventorySetup = new ItemInventorySetup(
                        item,
                        SECTION.getButtonData(getMenuID(), getButtonID()).getString("type"),
                        player,
                        quantity
                );
                itemInventorySetup.setMenuID(getMenuID());
                itemInventorySetup.setButtonID(getButtonID());
                // If success = item has been successfully created and sent to player inventory.
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
            } else {
                new ShopsForm().sendShopsForm(uuid);
            }
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }

    /**
     * Sell Item logic.
     */
    public void sellItem() {
        // Get Player Instance.
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        // Check if shop is disabled
        if (sellPrice == null) {
            assert player != null;
            form.content(Placeholders.colorCode(SECTION.getMessages("no-sell-price")));
            FloodgateApi.getInstance().sendForm(uuid, form.build());
            return;
        }

        assert player != null;
        if (!player.getInventory().contains(item)) {
            form.content(Placeholders.set(SECTION.getMessages("no-items"), item.name()));
            FloodgateApi.getInstance().sendForm(uuid, form.build());
            return;
        }

        form.content(Objects.requireNonNull(Placeholders.set(SECTION.getMenuData("confirmation").getString("content"), item.name(), sellPrice, quantity)));
        form.validResultHandler(response -> {
            if (response.clickedFirst()) {
                // Get an itemstack list from all items in player inventory.
                List<ItemStack> list = new ArrayList<>();
                for (ItemStack i : player.getInventory().getContents()) {
                    if (i != null)
                        list.add(i);
                }

                ItemStack[] inv = list.toArray(new ItemStack[0]);
                for (ItemStack fullinventory : inv) {
                    if (fullinventory == null) {
                        form.content(Placeholders.set(SECTION.getMessages("no-items"), item.name()));
                        FloodgateApi.getInstance().sendForm(uuid, form.build());
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
            } else {
                new ShopsForm().sendShopsForm(uuid);
            }
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
