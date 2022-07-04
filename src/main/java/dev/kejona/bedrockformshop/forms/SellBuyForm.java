package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class SellBuyForm {

    public static void buysellForm(UUID uuid, String itemStackName, String clickedButton, String category) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        CustomForm.Builder form = CustomForm.builder()
        .title(Utils.textPlaceholder((config.getString("form.buy-sell.title")), itemStackName))
        .toggle(Utils.colorCode(config.getString("form.buy-sell.buy-or-sell")), false)
        .slider(Utils.colorCode(config.getString("form.buy-sell.slider")), 0, 100);
        // Get item prices
        double buyPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".buy-price");
        double sellPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".sell-price");
        form.label(Utils.pricePlaceholder(config.getString("form.buy-sell.label"), buyPrice, sellPrice));
        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            int getAmount = (int) response.asSlider(1);

            if (response.asToggle(0)) {
                ItemHandler.sellItem(uuid, itemStackName, sellPrice, getAmount);
            } else {
                ItemHandler.buyItem(uuid, itemStackName, buyPrice, getAmount);
            }
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}