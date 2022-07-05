package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class SellBuyForm {

    public void buysellForm(UUID uuid, String itemStackName, String clickedButton, String category) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        CustomForm.Builder form = CustomForm.builder()
        .title(Placeholders.textPlaceholder((config.getString("form.buy-sell.title")), itemStackName))
        .toggle(Placeholders.colorCode(config.getString("form.buy-sell.buy-or-sell")), false)
        .slider(Placeholders.colorCode(config.getString("form.buy-sell.slider")), 0, 100);
        // Get item prices
        double buyPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".buy-price");
        double sellPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".sell-price");
        form.label(Placeholders.pricePlaceholder(config.getString("form.buy-sell.label"), buyPrice, sellPrice));
        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            ItemHandler itemHandler = new ItemHandler();
            int getAmount = (int) response.asSlider(1);

            if (response.asToggle(0)) {
                itemHandler.sellItem(uuid, itemStackName, sellPrice, getAmount);
            } else {
                itemHandler.buyItem(uuid, itemStackName, buyPrice, getAmount);
            }
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}