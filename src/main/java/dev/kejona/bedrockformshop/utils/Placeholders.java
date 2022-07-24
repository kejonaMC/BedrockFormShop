package dev.kejona.bedrockformshop.utils;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Placeholders {

    public static @NotNull String set(String text, String item) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static @NotNull String set(String text, String item, double price, int amount) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("%amount%", String.valueOf(amount))
                .replace("%price%", String.valueOf(price * amount))
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static @NotNull String set(String text, String command, double price) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%price%", String.valueOf(price)
                .replace("%command%", command)
                .toLowerCase(Locale.ROOT));
        return finalText;
    }

    public static @NotNull String set(String text, double buyPrice, double sellPrice) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%buyprice%", String.valueOf(buyPrice))
                .replace("%sellprice%", noPrice(sellPrice))
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static @NotNull String set(String text, @NotNull Player player) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%playername%", player.getDisplayName())
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    @Contract("_ -> new")
    public static @NotNull String colorCode(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static @NotNull String noPrice(double sellPrice) {
        FileConfiguration config = BedrockFormShop.getInstance().getConfig();
        String sellValue = String.valueOf(sellPrice);
        // If price is 0 then item is not sell-able.
        if (sellPrice == 0.0) {
            sellValue = config.getString("messages.no-sell-price");
        }

        assert sellValue != null;
        return sellValue;
    }
}
