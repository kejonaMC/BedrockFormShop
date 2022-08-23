package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.TransactionHandler;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class TransactionForm extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final UUID uuid;
    private final String object;
    private final boolean isCommand;

    public TransactionForm(UUID uuid, String object, boolean isCommand) {
        this.uuid = uuid;
        this.object = object;
        this.isCommand = isCommand;
    }

    // A form with item price and amount / command to buy or sell.
    public void sendTransactionForm() {
        // Item Prices.
        double buyPrice = SECTION.getButtonData(getMenuID(), getButtonID()).getDouble("buy-price");
        double sellPrice = SECTION.getButtonData(getMenuID(), getButtonID()).getDouble("sell-price");
        // Form Builder.
        CustomForm.Builder form = CustomForm.builder()
        .title(Placeholders.set((SECTION.getMenuData("buy-sell").getString("title")), object));
        // Check if config block is an item or command.
        if (!isCommand) {
                form.toggle(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("buy-or-sell")), false);
                form.slider(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("slider")), 1, 64);
                form.label(Placeholders.set(SECTION.getMenuData("buy-sell").getString("label"), buyPrice, sellPrice));
            }
            else {
                form.label(Placeholders.set(SECTION.getButtonData(getMenuID(), getButtonID()).getString("label"), buyPrice, sellPrice));
        }

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider.
            if (!isCommand) {
                TransactionHandler transactionHandler = new TransactionHandler(
                        uuid,
                        Material.getMaterial(object),
                        buyPrice,
                        sellPrice,
                        (int) response.asSlider(1)
                );

                transactionHandler.setButtonID(getButtonID());
                transactionHandler.setMenuID(getMenuID());
                // Form response
                if (response.asToggle(0)) {
                    if (sellPrice == 0.0) {
                        Player player = Bukkit.getPlayer(uuid);
                        assert player != null;
                        player.sendMessage(SECTION.getMessages("no-sell-price"));
                        return;
                    }
                    // Sell item.
                    transactionHandler.sellItem();
                } else {
                    // It's a normal item to buy
                    transactionHandler.buyItem();
                }
            }
            // If shopType is command inputs do not exist.
            else {
                CommandHandler commandHandler = new CommandHandler();
                commandHandler.executeCommand(uuid, object, buyPrice);
            }
        });

        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}