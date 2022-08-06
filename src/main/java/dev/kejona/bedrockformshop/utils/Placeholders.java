package dev.kejona.bedrockformshop.utils;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Placeholders {
    private static final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
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
        finalText = finalText.replace("%buyprice%", noBuyPrice(buyPrice))
                .replace("%sellprice%", noSellPrice(sellPrice))
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

    public static @NotNull String noSellPrice(double sellPrice) {

        String sellValue = String.valueOf(sellPrice);
        // If price is 0 then item is not sell-able.
        if (sellPrice == 0.0) {
            sellValue = SECTION.getMessages("no-sell-price");
        }

        assert sellValue != null;
        return sellValue;
    }

    public static @NotNull String noBuyPrice(double buyPrice) {

        String buyValue = String.valueOf(buyPrice);
        // If price is 0 then item is not sell-able.
        if (buyPrice == 0.0) {
            buyValue = SECTION.getMessages("no-buy-price");
        }

        assert buyValue != null;
        return buyValue;
    }
}
