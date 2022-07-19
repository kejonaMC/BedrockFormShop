package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class MainMenuForm {
    // A form with shop categories as buttons.
    public void mainMenu(UUID uuid) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();

        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Objects.requireNonNull(config.getString("form.menu.title")))
        .content(Objects.requireNonNull(config.getString("form.menu.content")));

        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form.menu.buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);

        for (String button : buttons) {
            String imageLocation = Objects.requireNonNull(config.getString("form.menu.buttons." + button + ".image"));
            // Check if image is url or path.
            if (imageLocation.startsWith("http")) {
                form.button(button, FormImage.Type.URL, imageLocation);
            }
            if (imageLocation.startsWith("kejona")) {
                // Image is path.
                form.button(button, FormImage.Type.URL, "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" + imageLocation.replace("kejona/", ""));
            } else {
                form.button(button, FormImage.Type.PATH, imageLocation);
            }
        }

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            // Send itemlist to player.
            ItemListForm itemList = new ItemListForm();
            itemList.itemList(uuid, buttons.get(response.clickedButtonId()), config);

        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
