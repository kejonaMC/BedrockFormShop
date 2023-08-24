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
    private final Logger logger = Logger.getLogger();
    private final UUID uuid;

    public ItemListForm(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Sends a form with all shop items as buttons + images.
     */
    public void sendItemListForm() {
        SimpleForm.Builder form = SimpleForm.builder();

        String shopName = getShopName();
        String shopTitle = SECTION.shopData(shopName).getString("title");
        String shopContent = SECTION.shopData(shopName).getString("content");

        form.title(Placeholders.set(shopTitle, shopName));
        form.content(Placeholders.set(shopContent, shopName));

        List<String> buttons = new ArrayList<>(SECTION.getButtons(shopName));
        List<String> noPermButtons = new ArrayList<>();

        for (String button : buttons) {
            Permission buttonPermission = Permission.valueOf(SECTION.itemData(shopName, button).getString("permission"));

            if (buttonPermission.checkItemPermission(uuid)) {
                String imageLocation = SECTION.itemData(shopName, button).getString("image");
                String itemName = SECTION.itemData(shopName, button).getString("item");

                String buttonTitle = SECTION.itemData(shopName, button).contains("button-title") ?
                        SECTION.itemData(shopName, button).getString("button-title") : button;

                FormImage image = ButtonImage.createFormImage(imageLocation, itemName);
                form.button(buttonTitle.replace("_", " "), image);
            } else {
                noPermButtons.add(button);
            }
        }

        buttons.removeAll(noPermButtons);

        form.validResultHandler(response -> {
            String clickedButtonName = buttons.get(response.clickedButtonId());
            setButtonName(clickedButtonName);

            String buttonTypeStr = SECTION.itemData(shopName, clickedButtonName).getString("type");
            if (buttonTypeStr != null) {
                ShopType buttonType = ShopType.valueOf(buttonTypeStr);

                switch (buttonType) {
                    case ITEM, ENCHANTMENT, POTION, SPAWNER -> {
                        TransactionForm transactionForm = new TransactionForm(uuid, false);
                        transactionForm.setShopName(shopName);
                        transactionForm.setButtonName(clickedButtonName);
                        transactionForm.sendTransactionForm();
                    }
                    case COMMAND -> {
                        TransactionForm transactionForm = new TransactionForm(uuid, true);
                        transactionForm.setShopName(shopName);
                        transactionForm.setButtonName(clickedButtonName);
                        transactionForm.sendTransactionForm();
                    }
                    case SUBSHOP -> {
                        ItemListForm listForm = new ItemListForm(uuid);
                        listForm.setShopName( SECTION.itemData(getShopName(), clickedButtonName).getString("linked-shop"));
                        listForm.sendItemListForm();
                    }
                    case BACK -> new ShopsForm().sendShopsForm(uuid);
                    case EXIT -> {
                        // Do nothing for EXIT type
                    }
                    default -> logger.severe("ShopType: " + buttonType + " is not a valid type!");
                }
            }
        });

        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}