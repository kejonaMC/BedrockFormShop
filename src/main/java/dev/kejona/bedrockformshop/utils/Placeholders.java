package dev.kejona.bedrockformshop.utils;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Placeholders {
    private static final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    public static @NotNull String set(String text, String item) {

        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("_", " ");
        return finalText;
    }

    public static @NotNull String set(String text, String item, BigDecimal price, int amount) {

        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("%amount%", String.valueOf(amount))
                .replace("%price%", String.valueOf(price.multiply(BigDecimal.valueOf(amount))))
                .replace("_", " ");
        return finalText;
    }

    public static @NotNull String set(String text, String command, BigDecimal price) {

        String finalText = colorCode(text);
        finalText = finalText.replace("%price%", String.valueOf(price))
                .replace("%command%", command);
        return finalText;
    }

    public static @NotNull String set(String text, BigDecimal buyPrice, BigDecimal sellPrice) {

        String finalText = text;
        finalText = finalText.replace("%buyprice%", noBuyPrice(buyPrice))
                .replace("%sellprice%", noSellPrice(sellPrice))
                .replace("_", " ");
        return finalText;
    }

    public static @NotNull String set(String text, @NotNull Player player) {

        String finalText = colorCode(text);
        finalText = finalText.replace("%player%", player.getName());
        return finalText;
    }

    @Contract("_ -> new")
    public static @NotNull String colorCode(String text) {

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static @NotNull String noSellPrice(BigDecimal sellPrice) {

        String sellValue = String.valueOf(sellPrice);
        // If price is 0 then item is not sell-able.
        if (sellPrice == null) {
            sellValue = colorCode(SECTION.getMessages("no-sell-price"));
        }

        assert sellValue != null;
        return sellValue;
    }

    public static @NotNull String noBuyPrice(BigDecimal buyPrice) {

        String buyValue = String.valueOf(buyPrice);
        // If price is 0 then item is not sell-able.
        if (buyPrice == null) {
            buyValue = colorCode(SECTION.getMessages("no-buy-price"));
            return buyValue;
        } else {
            if (buyPrice.doubleValue() == 0.0 || buyPrice.intValue() == 0) {
                buyValue = colorCode(SECTION.getMessages("no-price-set"));
                return buyValue;
            }
        }
        return buyValue;
    }
}
