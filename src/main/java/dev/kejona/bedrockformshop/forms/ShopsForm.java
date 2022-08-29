package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.formdata.ButtonImage;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.utils.Permission;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ShopsForm extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();

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
                // set image to button.
                assert imageLocation != null;
                FormImage image = ButtonImage.createFormImage(imageLocation, imageLocation);
                form.button(button.replace("_", " "), image);

            } else {
                noPermButtons.add(button);
            }
        }
        // Remove buttons that player does not have access to.
        buttons.removeAll(noPermButtons);

        form.validResultHandler(response -> {
            // Send item-list to player.
            ItemListForm listForm = new ItemListForm(uuid);
            listForm.setMenuID(buttons.get(response.clickedButtonId()));
            listForm.sendItemListForm();
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
