package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.handlers.ItemHandler;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class SellBuyForm {

    public void buysellForm(UUID uuid, String object, String clickedButton, String category, String shopType, FileConfiguration config) {
        // Item Prices
        double buyPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".buy-price");
        double sellPrice = config.getDouble("form." + category + ".buttons." + clickedButton + ".sell-price");
        // Form Builder
        CustomForm.Builder form = CustomForm.builder()
        .title(Placeholders.placeholder((config.getString("form.buy-sell.title")), object));
        // Check if config block is an item or command.
        if (shopType.equalsIgnoreCase("item")) {
            form.toggle(Placeholders.colorCode(config.getString("form.buy-sell.buy-or-sell")), false);
            form.slider(Placeholders.colorCode(config.getString("form.buy-sell.slider")), 0, 100);
            form.label(Placeholders.placeholder(config.getString("form.buy-sell.label"), buyPrice, sellPrice));

        } else if (shopType.equalsIgnoreCase("command")) {
            form.label(Placeholders.placeholder(config.getString("form." + category + ".buttons." + clickedButton + ".label"), buyPrice, sellPrice));
        }

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            // If shopType is item get the input from slider
            if (shopType.equalsIgnoreCase("item")) {
                ItemHandler itemHandler = new ItemHandler();
                int getAmount = (int) response.asSlider(1);

                if (response.asToggle(0)) {
                    itemHandler.sellItem(uuid, object, sellPrice, getAmount);
                } else {
                    itemHandler.buyItem(uuid, object, buyPrice, getAmount, config);
                }
            }
            // If shopType is command inputs do not exist
            if (shopType.equalsIgnoreCase("command")) {
                CommandHandler commandHandler = new CommandHandler();
                commandHandler.executeCommand(uuid, object, buyPrice, config);
            }
        });
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}