package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.PriceProvider;
import dev.kejona.bedrockformshop.handlers.TransactionHandler;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
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
    /**
     * A form with item price and amount / command to buy or sell.
     */
    public void sendTransactionForm() {
        // Item Prices.
        PriceProvider price = new PriceProvider(getMenuID(), getButtonID());
        // Form Builder.
        CustomForm.Builder form = CustomForm.builder();
        // Check if config block is an item or command.
        if (!isCommand) {
            setBuyPrice(price.buyPrice(Material.valueOf(object)));
            setSellPrice(price.sellPrice(Material.valueOf(object)));
            form.title(Placeholders.set((SECTION.getMenuData("buy-sell").getString("title")), object));
            form.toggle(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("buy-or-sell")), false);
            form.slider(Placeholders.colorCode(SECTION.getMenuData("buy-sell").getString("slider")), 1, SECTION.getMenuData("buy-sell").getInt("max-slider"));
            form.label(Placeholders.set(SECTION.getMenuData("buy-sell").getString("label"), getBuyPrice(), getSellPrice()));
        } else {
            setBuyPrice(price.defaultBuyPrice());
            form.title(Placeholders.set(SECTION.getButtonData(getMenuID(), getButtonID()).getString("title"), object));
            form.label(Placeholders.set(SECTION.getButtonData(getMenuID(), getButtonID()).getString("label"), getBuyPrice(), getSellPrice()));
        }

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider.
            if (!isCommand) {
                TransactionHandler transactionHandler = new TransactionHandler(
                        uuid,
                        Material.getMaterial(object),
                        getBuyPrice(),
                        getSellPrice(),
                        (int) response.asSlider(1)
                );

                transactionHandler.setButtonID(getButtonID());
                transactionHandler.setMenuID(getMenuID());
                // Form response
                if (response.asToggle(0)) {
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
                commandHandler.executeCommand(uuid, object, getBuyPrice());
            }
        });

        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}