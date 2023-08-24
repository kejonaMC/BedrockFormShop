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
import org.jetbrains.annotations.NotNull;

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
        form.title(Objects.requireNonNull(SECTION.shopFormData("confirmation").getString("title")));
        form.button1(Objects.requireNonNull(SECTION.shopFormData("confirmation").getString("button1")));
        form.button2(Objects.requireNonNull(SECTION.shopFormData("confirmation").getString("button2")));
    }

    /**
     * Buy Item logic.
     */
    public void buyItem() {
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        assert player != null;

        // Check if item is not buy-able.
        if (buyPrice == null) {
            form.content(Placeholders.colorCode(SECTION.getMessages("no-buy-price")));
            sendForm(form);
            return;
        }

        BigDecimal totalBuyPrice = buyPrice.multiply(BigDecimal.valueOf(quantity));

        // Check if player has enough balance.
        if (VaultAPI.eco().getBalance(player).compareTo(totalBuyPrice) < 0) {
            form.content(Placeholders.colorCode(SECTION.getMessages("not-enough-money")));
            sendForm(form);
            return;
        }

        // Player can buy the items.
        String content = Placeholders.set(SECTION.shopFormData("confirmation").getString("content"), item.name(), buyPrice, quantity);
        form.content(Objects.requireNonNull(content));

        form.validResultHandler(response -> {
            if (response.clickedFirst()) {
                performBuy(player, totalBuyPrice);
            } else {
                new ShopsForm().sendShopsForm(uuid);
            }
        });

        sendForm(form);
    }

    private void performBuy(Player player, BigDecimal totalBuyPrice) {
        ItemInventorySetup itemInventorySetup = new ItemInventorySetup(item, SECTION.itemData(getShopName(), getButtonName()).getString("type"), player, quantity);
        itemInventorySetup.setShopName(getShopName());
        itemInventorySetup.setButtonName(getButtonName());

        if (itemInventorySetup.buyItemSuccess()) {
            VaultAPI.eco().withdrawBalance(player, totalBuyPrice);
            try {
                new FileWriterShopOutput(player.getName(), buyPrice, quantity, item.name());
            } catch (IOException e) {
                logger.severe("Could not save shop data." + e.getMessage());
            }
            player.sendMessage(Placeholders.set(SECTION.getMessages("item-bought"), itemInventorySetup.getItemName(), buyPrice, quantity));
        }
    }

    /**
     * Sell Item logic.
     */
    public void sellItem() {
        Player player = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        assert player != null;

        // Check if item is not sell-able.
        if (sellPrice == null) {
            form.content(Placeholders.colorCode(SECTION.getMessages("no-sell-price")));
            sendForm(form);
            return;
        }

        // Check if player has enough items to sell.
        int itemCount = countItemInInventory(player, item);
        if (itemCount < quantity) {
            form.content(Placeholders.set(SECTION.getMessages("not-enough-items"), item.name()));
            sendForm(form);
            return;
        }

        String content = Placeholders.set(SECTION.shopFormData("confirmation").getString("content"), item.name(), sellPrice, quantity);
        form.content(Objects.requireNonNull(content));

        form.validResultHandler(response -> {
            if (response.clickedFirst()) {
                performSell(player);
            } else {
                new ShopsForm().sendShopsForm(uuid);
            }
        });

        sendForm(form);
    }

    private void performSell(@NotNull Player player) {
        int remainingQuantity = quantity;
        ItemStack[] playerInventory = player.getInventory().getContents();

        for (int i = 0; i < playerInventory.length && remainingQuantity > 0; i++) {
            ItemStack currentItem = playerInventory[i];
            if (currentItem != null && currentItem.getType() == item) {
                int currentAmount = currentItem.getAmount();
                if (currentAmount >= remainingQuantity) {
                    currentItem.setAmount(currentAmount - remainingQuantity);
                    remainingQuantity = 0;
                } else {
                    player.getInventory().setItem(i, null);
                    remainingQuantity -= currentAmount;
                }
            }
        }

        BigDecimal totalSellPrice = sellPrice.multiply(BigDecimal.valueOf(quantity));
        VaultAPI.eco().depositBalance(player, totalSellPrice);

        try {
            new FileWriterShopOutput(player.getName(), sellPrice, quantity, item.name());
        } catch (IOException e) {
            logger.severe("Could not save shop data." + e.getMessage());
        }

        player.sendMessage(Placeholders.set(SECTION.getMessages("item-sold"), item.name(), sellPrice, quantity));
    }

    // Utility method to count the quantity of a specific item in a player's inventory
    private int countItemInInventory(@NotNull Player player, Material material) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == material) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }

    // Utility method to send a form to the player
    private void sendForm(ModalForm.@NotNull Builder form) {
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}