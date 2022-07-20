package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.handlers.ShopType;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SellBuyForm {
    public FileConfiguration config = BedrockFormShop.getInstance().getConfig();
    // A form with item price and amount / command to buy or sell.
    public void buysellForm(UUID uuid, String object, String clickedButton, String category, @NotNull String shopType) {
        // Item Prices.
        double buyPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".buy-price");
        double sellPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".sell-price");
        // Form Builder.
        CustomForm.Builder form = CustomForm.builder()
        .title(Placeholders.placeholder((config.getString("form.buy-sell.title")), object));
        // Check if config block is an item or command.
        if (ShopType.ITEM.name().equals(shopType) || ShopType.ENCHANTMENT.name().equals(shopType) || ShopType.POTION.name().equals(shopType)) {
            form.toggle(Placeholders.colorCode(config.getString("form.buy-sell.buy-or-sell")), false);
            form.slider(Placeholders.colorCode(config.getString("form.buy-sell.slider")), 0, 100);
            form.label(Placeholders.placeholder(config.getString("form.buy-sell.label"), buyPrice, sellPrice));
        }
        if (ShopType.COMMAND.name().equals(shopType)) {
            form.label(Placeholders.placeholder(config.getString("form." + category + ".buttons." + clickedButton + ".label"), buyPrice, sellPrice));
        }

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider.
            if (ShopType.ITEM.name().equals(shopType) || ShopType.ENCHANTMENT.name().equals(shopType) || ShopType.POTION.name().equals(shopType)) {
                ItemHandler itemHandler = new ItemHandler();
                int getAmount = (int) response.asSlider(1);

                if (response.asToggle(0)) {
                    if (sellPrice == 0.0) {
                        Player player = Bukkit.getPlayer(uuid);
                        assert player != null;
                        player.sendMessage(Objects.requireNonNull(config.getString("messages.no-sell-price")));
                        return;
                    }
                    itemHandler.sellItem(uuid, object, sellPrice, getAmount);
                } else {
                    // Check for enchantments.
                    boolean isEnchantment = ShopType.ENCHANTMENT.name().equals(shopType);
                    boolean isPotion = ShopType.POTION.name().equals(shopType);
                    String dataPath = null;

                    if (isEnchantment) {
                        dataPath = "form." + category + ".buttons." + clickedButton + ".enchantment";
                    }
                    if (isPotion) {
                        dataPath = "form." + category + ".buttons." + clickedButton + ".potion-data";
                    }
                    itemHandler.buyItem(uuid, object, buyPrice, getAmount, dataPath, isEnchantment, isPotion);
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