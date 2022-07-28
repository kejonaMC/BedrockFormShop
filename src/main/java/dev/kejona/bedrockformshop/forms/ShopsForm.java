package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.config.Configuration;
import dev.kejona.bedrockformshop.utils.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ShopsForm {
    public ConfigurationSection SECTION;
    // A form with shop categories as buttons.
    public void sendShopsForm(UUID uuid) {
        SECTION = Configuration.getMenuData("menu");
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Objects.requireNonNull(SECTION.getString("title")))
        .content(Objects.requireNonNull(SECTION.getString("content")));
        // Get all Buttons in config.
        List<String> buttons = new ArrayList<>(Configuration.getButtons("menu"));
        List<String> noPermButtons = new ArrayList<>();

        for (String button : buttons) {
            SECTION = Configuration.getButtonData("menu", button);
            // Check if player has permission to this button. if not button will not be generated.
            if (Permission.valueOf(SECTION.getString("permission")).checkPermission(uuid)) {
                String imageLocation = SECTION.getString("image");
                // Check if image is url or path.

                assert imageLocation != null;
                if (imageLocation.startsWith("http")) {
                    form.button(button, FormImage.Type.URL, imageLocation);
                }
                // If location is kejona we use the images from our github repo
                if (imageLocation.startsWith("default")) {
                    // Image is path.
                    form.button(button, FormImage.Type.URL, "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" + imageLocation.replace("default/", ""));
                } else {
                    form.button(button, FormImage.Type.PATH, imageLocation);
                }
            } else {
                noPermButtons.add(button);
            }
        }
        // Remove buttons that player does not have access to.
        buttons.removeAll(noPermButtons);

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            // Send itemlist to player.
            ItemListForm itemListForm = new ItemListForm();
            itemListForm.sendItemListForm(uuid, buttons.get(response.clickedButtonId()));
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
