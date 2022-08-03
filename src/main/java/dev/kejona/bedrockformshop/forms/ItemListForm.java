package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import dev.kejona.bedrockformshop.utils.ShopType;
import dev.kejona.bedrockformshop.utils.Permission;
import dev.kejona.bedrockformshop.utils.Placeholders;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ItemListForm {
    public ConfigurationHandler SECTION = BedrockFormShop.getInstance().getConfigurationHandler();
    Logger logger = Logger.getLogger();

    // A form with all shop items as buttons.
    public void sendItemListForm(UUID uuid, String menuID) {
        SECTION.getMenuData(menuID);
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Placeholders.set(SECTION.getMenuData(menuID).getString("title"), menuID))
        .content(Placeholders.set(SECTION.getMenuData(menuID).getString("content"), menuID));
        // Get all Buttons in config.
        List<String> buttons = new ArrayList<>(SECTION.getButtons(menuID));
        List<String> noPermButtons = new ArrayList<>();
        // Loop all buttons and add them to form.
        for (String button : buttons) {
            // Check if player has permission to this button. if not button will not be generated.
            if (Permission.valueOf(SECTION.getButtonData(menuID, button).getString("permission")).checkPermission(uuid)) {
                String imageLocation = SECTION.getButtonData(menuID, button).getString("image");
                String getItemName = SECTION.getButtonData(menuID, button).getString("item");
                // Check if image is url or path.
                if (imageLocation != null) {
                    // Image default will get images from our github repo.
                    if (imageLocation.equalsIgnoreCase("default")) {
                        // Image is path.
                        assert getItemName != null;
                        form.button(button.replace("_", " "), FormImage.Type.URL, "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" + getItemName.toLowerCase() + ".png");
                    } else if (imageLocation.startsWith("http")) {
                        // Image is url.
                        form.button(button.replace("_", " "), FormImage.Type.URL, imageLocation);
                    } else {
                        // Image is path.
                        form.button(button.replace("_", " "), FormImage.Type.PATH, imageLocation);
                    }
                }
            } else {
                buttons.removeAll(noPermButtons);
            }
        }
        // response is valid
        form.validResultHandler(response -> {
            String clickedButton = buttons.get(response.clickedButtonId());

            TransactionForm transaction = new TransactionForm();
            // Check shop type
            switch (ShopType.valueOf(SECTION.getButtonData(menuID, clickedButton).getString("type"))) {

                case ITEM, ENCHANTMENT, POTION, SPAWNER -> {
                    String itemStackName = SECTION.getButtonData(menuID, clickedButton).getString("item");
                    transaction.sendTransactionForm(uuid, itemStackName, clickedButton, menuID, false);
                }

                case COMMAND -> {
                    String command = SECTION.getButtonData(menuID, clickedButton).getString("command");
                    transaction.sendTransactionForm(uuid, command, clickedButton, menuID, true);
                }
                // default gets triggered if no type is set or not matched.
                default -> logger.severe("ShopType: " + ShopType.valueOf(SECTION.getButtonData(menuID, clickedButton).getString("type")) + " is not a valid type!");
            }
        });

        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
