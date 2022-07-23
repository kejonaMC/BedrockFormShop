package dev.kejona.bedrockformshop.forms;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.utils.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public class MainMenuForm {

    public FileConfiguration config = BedrockFormShop.getInstance().getConfig();
    // A form with shop categories as buttons.
    public void mainMenu(UUID uuid) {
        // Form Builder
        SimpleForm.Builder form = SimpleForm.builder()
        .title(Objects.requireNonNull(config.getString("form.menu.title")))
        .content(Objects.requireNonNull(config.getString("form.menu.content")));

        // Get all Buttons in config.
        Set<String> listButtons = Objects.requireNonNull(config.getConfigurationSection("form.menu.buttons")).getKeys(false);
        List<String> buttons = new ArrayList<>(listButtons);
        List<String> disabledButtons = new ArrayList<>();

        for (String button : buttons) {
            // Check if player has permission to this button. if not button will not be generated.
            String getPerm = config.getString("form.menu.buttons." + button + ".permission");

            if (Permission.valueOf(getPerm).checkPermission(uuid)) {
                String imageLocation = Objects.requireNonNull(config.getString("form.menu.buttons." + button + ".image"));
                // Check if image is url or path.
                if (imageLocation.startsWith("http")) {
                    form.button(button, FormImage.Type.URL, imageLocation);
                }
                // If location is kejona we use the images from our github repo
                if (imageLocation.startsWith("kejona")) {
                    // Image is path.
                    form.button(button, FormImage.Type.URL, "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" + imageLocation.replace("kejona/", ""));
                } else {
                    form.button(button, FormImage.Type.PATH, imageLocation);
                }
            } else {
                disabledButtons.add(button);
            }
        }
        // Remove buttons that player does not have access to.
        buttons.removeAll(disabledButtons);

        // Handle buttons responses.
        form.closedOrInvalidResultHandler(response -> {
            response.isClosed();
            response.isInvalid();
        });

        form.validResultHandler(response -> {
            // Send itemlist to player.
            ItemListForm itemList = new ItemListForm();
            System.out.println(buttons + "fjlsdjkflkj");
            itemList.itemList(uuid, buttons.get(response.clickedButtonId()));
        });
        // Build form and send to player.
        FloodgateApi.getInstance().sendForm(uuid, form.build());
    }
}
