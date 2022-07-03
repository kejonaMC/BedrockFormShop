package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class SellBuyForm {

    public static void buysellForm(UUID uuid, String itemStackName,String clickedButton, String category) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        CustomForm.Builder form = CustomForm.builder();
        form.title(Utils.textPlaceholder((config.getString("form.buy-sell.title")), itemStackName));
        form.toggle(Utils.colorCode(config.getString("form.buy-sell.buy-or-sell")),false);
        form.slider(Utils.colorCode(config.getString("form.buy-sell.slider")), 0, 100);
        // Get item prices
        double buyPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".buy-price");
        double sellPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".sell-price");
        form.label(Utils.pricePlaceholder(config.getString("form.buy-sell.label"), buyPrice, sellPrice));
        // Handle buttons responses.
        form.responseHandler((responseData) -> {
            CustomFormResponse response = form.build().parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }
            // results
            int getAmount = (int) response.getSlider(1);

            if (response.getToggle(0)) {
                ItemHandler.sellItem(uuid, itemStackName, sellPrice, getAmount);
            } else {
                ItemHandler.buyItem(uuid, itemStackName, buyPrice, getAmount);
            }
            // get Item from config.
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
