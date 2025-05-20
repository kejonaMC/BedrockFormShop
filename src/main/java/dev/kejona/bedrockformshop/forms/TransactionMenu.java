package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.PriceProvider;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.Material;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class TransactionMenu extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final UUID uuid;
    private String item;
    private final boolean isCommand;

    public TransactionMenu(UUID uuid, boolean isCommand) {
        this.uuid = uuid;
        this.isCommand = isCommand;
    }
    /**
     * A form with item price and amount / command to buy or sell.
     */
    public void sendTransactionForm() {
        // Item Prices.
        PriceProvider price = new PriceProvider(getShopName(), getButtonName());
        setBuyPrice(price.buyPrice());
        // Form Builder.
        CustomForm.Builder form = CustomForm.builder();
        // Check if config block is an item or command.
        if (!isCommand) {
            item = SECTION.itemData(getShopName(), getButtonName()).getString("item");
            setSellPrice(price.sellPrice());
            form.title(Placeholders.set((SECTION.shopFormData("buy-sell").getString("title")), item));
            form.toggle(Placeholders.colorCode(SECTION.shopFormData("buy-sell").getString("buy-or-sell")), false);

            if (SECTION.itemData(getShopName(), getButtonName()).getInt("max-item") == 0) {
                form.slider(Placeholders.colorCode(SECTION.shopFormData("buy-sell").getString("slider")), 1, SECTION.shopFormData("buy-sell").getInt("max-slider"));
            } else {
                form.slider(Placeholders.colorCode(SECTION.shopFormData("buy-sell").getString("slider")), 1, SECTION.itemData(getShopName(), getButtonName()).getInt("max-item"));
            }

            form.label(Placeholders.set(SECTION.shopFormData("buy-sell").getString("label"), getBuyPrice(), getSellPrice()));

        } else {
            form.title(Placeholders.colorCode(SECTION.itemData(getShopName(), getButtonName()).getString("title")));
            form.label(Placeholders.set(SECTION.itemData(getShopName(), getButtonName()).getString("label"), getBuyPrice(), getSellPrice()));
        }

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider.
            if (!isCommand) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(
                        uuid,
                        Material.getMaterial(item),
                        getBuyPrice(),
                        getSellPrice(),
                        (int) response.asSlider(1)
                );

                confirmationMenu.setButtonName(getButtonName());
                confirmationMenu.setShopName(getShopName());
                // Form response
                if (response.asToggle(0)) {
                    // Sell item.
                    confirmationMenu.sellItem();
                } else {
                    // It's a normal item to buy
                    confirmationMenu.buyItem();
                }
            }
            // If shopType is command inputs do not exist.
            else {
                new CommandHandler().executeCommand(
                        uuid,
                        SECTION.itemData(getShopName(), getButtonName()).getStringList("commands"),
                        getBuyPrice());
            }
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}