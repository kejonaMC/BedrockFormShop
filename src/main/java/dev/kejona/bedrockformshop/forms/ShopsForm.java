package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.utils.Permission;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ShopsForm {
    public ConfigurationHandler SECTION = BedrockFormShop.getInstance().getConfigurationHandler();
    // A form with shop categories as buttons.
    public void sendShopsForm(UUID uuid) {
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Objects.requireNonNull(SECTION.getMenuData("menu").getString("title")))
        .content(Objects.requireNonNull(SECTION.getMenuData("menu").getString("content")));
        // Get all Buttons in config.
        List<String> buttons = new ArrayList<>(SECTION.getButtons("menu"));
        List<String> noPermButtons = new ArrayList<>();

        for (String button : buttons) {
            // Check if player has permission to this button. if not button will not be generated.
            if (Permission.valueOf(SECTION.getButtonData("menu", button).getString("permission")).checkPermission(uuid)) {
                String imageLocation = SECTION.getButtonData("menu", button).getString("image");
                // Check if image is url or path.

                assert imageLocation != null;
                if (imageLocation.startsWith("http")) {
                    form.button(button, FormImage.Type.URL, imageLocation);
                }
                // If location is kejona we use the images from our GitHub repo
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

        form.validResultHandler(response -> {
            // Send itemlist to player.
            ItemListForm itemListForm = new ItemListForm();
            itemListForm.sendItemListForm(uuid, buttons.get(response.clickedButtonId()));
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
