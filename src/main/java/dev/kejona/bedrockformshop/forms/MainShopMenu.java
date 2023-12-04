package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.formdata.ButtonImage;
import dev.kejona.bedrockformshop.handlers.CommandHandler;
import dev.kejona.bedrockformshop.shopdata.ShopData;
import dev.kejona.bedrockformshop.shopdata.ShopType;
import dev.kejona.bedrockformshop.utils.Permission;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.math.BigDecimal;
import java.util.*;

public class MainShopMenu extends ShopData {

    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();

    /**
     * Sends a form with shop categories as buttons.
     */
    public void sendShopsForm(UUID uuid) {
        // Create the form builder
        SimpleForm.Builder form = SimpleForm.builder();

        // Set form title and content from configuration
        form.title(Objects.requireNonNull(SECTION.shopFormData("menu").getString("title")));
        form.content(Objects.requireNonNull(SECTION.shopFormData("menu").getString("content")));

        List<String> shops = new ArrayList<>(SECTION.getShops());
        List<String> noPermButtons = new ArrayList<>();

        for (String shop : shops) {
            boolean shouldShowInMenu = SECTION.shopData(shop).getBoolean("show-in-menu");
            boolean hasPermission = Permission.OPEN_SHOP.checkShopPermission(uuid, shop.toLowerCase());

            if (shouldShowInMenu && hasPermission) {
                String imageLocation = SECTION.shopData(shop).getString("image");
                String buttonTitle = SECTION.shopData(shop).contains("button-title") ?
                        SECTION.shopData(shop).getString("button-title") : shop;
                FormImage image = ButtonImage.createFormImage(imageLocation, imageLocation);

                form.button(buttonTitle.replace("_", " "), image);
            } else {
                noPermButtons.add(shop);
            }
        }

        // Remove buttons that player does not have access to
        shops.removeAll(noPermButtons);

        form.validResultHandler(response -> {
            String clickedShop = shops.get(response.clickedButtonId());
            setButtonName(clickedShop);

            // Check if the shop type is an exit shop
            String shopTypeStr = SECTION.shopData(getButtonName()).getString("type");
            if (shopTypeStr != null && ShopType.EXIT.equals(ShopType.valueOf(shopTypeStr))) {
                return;
            }

            List<String> commands = SECTION.shopData(getButtonName()).getStringList("commands");
            if (!commands.isEmpty()) {
                new CommandHandler().executeCommand(uuid, commands, BigDecimal.ZERO);
                return;
            }

            // Send item-list to player
            ItemListMenu listForm = new ItemListMenu(uuid);
            listForm.setShopName(getButtonName());
            listForm.sendItemListForm();
        });

        // Build and send the form to the player
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}