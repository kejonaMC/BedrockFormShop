package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.formdata.ButtonImage;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.shopdata.ShopType;
import dev.kejona.bedrockformshop.utils.*;
import dev.kejona.bedrockformshop.logger.Logger;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class ItemListForm extends ShopData {
    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    Logger logger = Logger.getLogger();
    private final UUID uuid;

    public ItemListForm(UUID uuid) {
        this.uuid = uuid;
    }

    // A form with all shop items as buttons.
    public void sendItemListForm() {
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Placeholders.set(SECTION.getMenuData(getMenuID()).getString("title"), getMenuID()))
        .content(Placeholders.set(SECTION.getMenuData(getMenuID()).getString("content"), getMenuID()));
        // Get all Buttons in config.
        List<String> buttons = new ArrayList<>(SECTION.getButtons(getMenuID()));
        List<String> noPermButtons = new ArrayList<>();
        // Loop all buttons and add them to form.
        for (String button : buttons) {
            // Check if player has permission to this button. if not button will not be generated.
            if (Permission.valueOf(SECTION.getButtonData(getMenuID(), button).getString("permission")).checkPermission(uuid)) {
                String imageLocation = SECTION.getButtonData(getMenuID(), button).getString("image");
                String getItemName = SECTION.getButtonData(getMenuID(), button).getString("item");
                // set image to button.
                FormImage image = ButtonImage.createFormImage(imageLocation, getItemName);
                form.button(button.replace("_", " "), image);

            } else {
                buttons.removeAll(noPermButtons);
            }
        }
        // response is valid
        form.validResultHandler(response -> {
            setButtonID(buttons.get(response.clickedButtonId()));
            // Check shop type
            switch (ShopType.valueOf(SECTION.getButtonData(getMenuID(), getButtonID()).getString("type"))) {

                case ITEM, ENCHANTMENT, POTION, SPAWNER -> {
                    String itemStackName = SECTION.getButtonData(getMenuID(), getButtonID()).getString("item");
                    TransactionForm transactionForm = new TransactionForm(uuid, itemStackName, false);
                    transactionForm.setMenuID(getMenuID());
                    transactionForm.setButtonID(buttons.get(response.clickedButtonId()));
                    transactionForm.sendTransactionForm();
                }

                case COMMAND -> {
                    String command = SECTION.getButtonData(getMenuID(), getButtonID()).getString("command");
                    TransactionForm transactionForm = new TransactionForm(uuid, command, true);
                    transactionForm.setMenuID(getMenuID());
                    transactionForm.setButtonID(buttons.get(response.clickedButtonId()));
                    transactionForm.sendTransactionForm();
                }
                // default gets triggered if no type is set or not matched.
                default -> logger.severe("ShopType: " + ShopType.valueOf(SECTION.getButtonData(getMenuID(), getButtonID()).getString("type")) + " is not a valid type!");
            }
        });

        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
