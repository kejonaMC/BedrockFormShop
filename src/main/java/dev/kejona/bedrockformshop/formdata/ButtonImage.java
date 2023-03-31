package dev.kejona.bedrockformshop.formdata;

import dev.kejona.bedrockformshop.BedrockFormShop;

import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Nullable;

public class ButtonImage {
    /**
     * Adds an image to a form
     * @param data image location
     * @param extraData item name
     * @return formImage
     */
    @Nullable
    public static FormImage createFormImage(@Nullable String data, @Nullable String extraData) {
        if (data == null || data.isEmpty()) {
            return null;
        } else {
            FormImage.Type type;
            if (data.startsWith("default")) {
                type = FormImage.Type.URL;
                assert extraData != null;
                return FormImage.of(type,
                        "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" +
                                BedrockFormShop.getInstance().getServerVersion() + "/" +
                                extraData.toLowerCase()
                                        .replace("default/", "") + ".png");
            }
            if (data.startsWith("http")) {
                type = FormImage.Type.URL;
            } else {
                type = FormImage.Type.PATH;
            }
            return FormImage.of(type, data);
        }
    }
}
