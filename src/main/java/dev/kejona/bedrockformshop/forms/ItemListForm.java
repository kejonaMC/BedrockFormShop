package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ItemListForm {
    public static void itemList(UUID uuid, String category) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Utils.textPlaceholder(Objects.requireNonNull(config.getString("form." + category + ".title")), category))
        .content(Utils.textPlaceholder(Objects.requireNonNull(config.getString("form."+ category +".content")), category));
        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form."+ category +".buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);
        // Loop all buttons and add them to form.
        for (String button : buttons) {
            form.button(button, FormImage.Type.URL, Objects.requireNonNull(config
                    .getString("form."+ category +".buttons." + button + ".image")));

        }

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });
        // response is valid
        form.validResultHandler(response -> {
            String clickedButton = buttons.get(response.clickedButtonId());
            String itemStackName = config.getString("form." + category + ".buttons." + clickedButton + ".item");
            SellBuyForm.buysellForm(uuid, itemStackName, clickedButton, category);
        });


        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
