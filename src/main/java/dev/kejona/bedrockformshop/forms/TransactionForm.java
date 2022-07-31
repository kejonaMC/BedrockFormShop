package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.utils.ShopType;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TransactionForm {
    public ConfigurationHandler SECTION = BedrockFormShop.getInstance().getConfigurationHandler();
    // A form with item price and amount / command to buy or sell.
    public void sendTransactionForm(UUID uuid, String object, String clickedButton, String menuID, @NotNull String shopType) {
        // Item Prices.
        double buyPrice = SECTION.getButtonData(menuID, clickedButton).getDouble("buy-price");
        double sellPrice = SECTION.getButtonData(menuID, clickedButton).getDouble("sell-price");
        // Form Builder.
        CustomForm.Builder form = CustomForm.builder()
        .title(Placeholders.set((SECTION.getMenuData("buy-sell").getString("title")), object));
        // Check if config block is an item or command.
        if (ShopType.ITEM.name().equals(shopType) || ShopType.ENCHANTMENT.name().equals(shopType) || ShopType.POTION.name().equals(shopType) || ShopType.SPAWNER.name().equals(shopType)) {
            form.toggle(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("buy-or-sell")), false);
            form.slider(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("slider")), 0, 100);
            form.label(Placeholders.set(SECTION.getMenuData("buy-sell").getString("label"), buyPrice, sellPrice));
        }

        if (ShopType.COMMAND.name().equals(shopType)) {
            form.label(Placeholders.set(SECTION.getMenuData("buy-sell").getString("label"), buyPrice, sellPrice));
        }

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider.
            if (ShopType.ITEM.name().equals(shopType) || ShopType.ENCHANTMENT.name().equals(shopType) || ShopType.POTION.name().equals(shopType) || ShopType.SPAWNER.name().equals(shopType)) {
                ItemHandler itemHandler = new ItemHandler();
                int getAmount = (int) response.asSlider(1);
                // Form response
                if (response.asToggle(0)) {
                    if (sellPrice == 0.0) {
                        Player player = Bukkit.getPlayer(uuid);
                        assert player != null;
                        player.sendMessage(SECTION.getMessages("no-sell-price"));
                        return;
                    }
                    // Sell item.
                    itemHandler.sellItem(uuid, object, sellPrice, getAmount);
                } else {
                    // Its a normal item to buy
                    itemHandler.buyItem(uuid, object, buyPrice, getAmount,menuID, clickedButton, shopType);
                }
            }
            // If shopType is command inputs do not exist.
            if (ShopType.COMMAND.name().equals(shopType)) {
                CommandHandler commandHandler = new CommandHandler();
                commandHandler.executeCommand(uuid, object, buyPrice);
            }
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}