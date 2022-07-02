package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ItemListForm {
    public static void itemList(UUID uuid, String category) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder();
        form.title(Utils.textPlaceholder(Objects.requireNonNull(config.getString("form." + category + ".title")), category));
        form.content(Utils.textPlaceholder(Objects.requireNonNull(config.getString("form."+ category +".content")), category));

        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form."+ category +".buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);

        for (String button : buttons) {
            form.button(button, FormImage.Type.URL, Objects.requireNonNull(config
                    .getString("form."+ category +".buttons." + button + ".image")));

        }

        // Handle buttons responses.
        form.responseHandler((responseData) -> {
            SimpleFormResponse response = form.build().parseResponse(responseData);
            if (!response.isCorrect()) {
                return;
            }
            String clickedButton = buttons.get(response.getClickedButtonId());
            String itemStackName = config.getString("form." + category + ".buttons." + clickedButton + ".item");
            SellBuyForm.buysellForm(uuid, itemStackName, clickedButton, category);

        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
