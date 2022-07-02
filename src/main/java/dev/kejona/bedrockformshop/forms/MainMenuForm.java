package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class MainMenuForm {

    public static void mainMenu(UUID uuid) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder();
        form.title(Objects.requireNonNull(config.getString("form.menu.title")));
        form.content(Objects.requireNonNull(config.getString("form.menu.content")));

        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form.menu.buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);

        for (String button : buttons) {
            form.button(button, FormImage.Type.URL, Objects.requireNonNull(config
                    .getString("form.menu.buttons." + button + ".image")));

        }

        // Handle buttons responses.
        form.responseHandler((responseData) -> {
            SimpleFormResponse response = form.build().parseResponse(responseData);
            if (!response.isCorrect()) {
                return;
            }
            // Send itemlist to player
            ItemListForm.itemList(uuid, buttons.get(response.getClickedButtonId()));

        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
