package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.utils.Placeholders;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ItemListForm {
    public void itemList(UUID uuid, String category, FileConfiguration config) {
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Placeholders.placeholder(config.getString("form." + category + ".title"), category))
        .content(Placeholders.placeholder(config.getString("form."+ category +".content"), category));
        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form." + category + ".buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);
        // Loop all buttons and add them to form.
        for (String button : buttons) {
            String imageLocation = config.getString("form."+ category +".buttons."+ button +".image");
            // Check if image is url or path.
            if (imageLocation != null) {
                if (imageLocation.startsWith("http")) {
                    form.button(button, FormImage.Type.URL, imageLocation);
                } else {
                    form.button(button, FormImage.Type.PATH, imageLocation);
                }
            }
        }

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });
        // response is valid
        form.validResultHandler(response -> {
            String clickedButton = buttons.get(response.clickedButtonId());
            String shopType = config.getString("form." + category + ".buttons." + clickedButton + ".type");

            if (shopType != null && shopType.equalsIgnoreCase("item")) {
                String itemStackName = config.getString("form." + category + ".buttons." + clickedButton + ".item");
                SellBuyForm sell = new SellBuyForm();
                sell.buysellForm(uuid, itemStackName, clickedButton, category, shopType, config);
            }

            if (shopType != null && shopType.equalsIgnoreCase("command")) {
                String command = config.getString("form." + category + ".buttons." + clickedButton + ".command");
                SellBuyForm buy = new SellBuyForm();
                buy.buysellForm(uuid, command, clickedButton, category, shopType, config);
            }
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
