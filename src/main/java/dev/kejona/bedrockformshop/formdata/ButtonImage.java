package dev.kejona.bedrockformshop.formdata;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.geysermc.cumulus.util.FormImage;

public class ButtonImage {
    // The image location
    @Nullable
    public static FormImage createFormImage(@Nullable String data,@Nullable String extraData) {
        if (data == null || data.isEmpty()) {
            return null;
        } else {
            FormImage.Type type;
            if (data.startsWith("default")) {
                type = FormImage.Type.URL;
                assert extraData != null;
                return FormImage.of(type, "https://raw.githubusercontent.com/Jens-Co/MinecraftItemImages/main/" +  extraData.toLowerCase().replace("default/", "") + ".png");
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
