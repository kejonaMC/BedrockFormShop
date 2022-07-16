package dev.kejona.bedrockformshop.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Placeholders {

    public static String placeholder(String text, String item) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static String placeholder(String text, String item, double price, int amount) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%item%", item)
                .replace("%price%", String.valueOf(price * amount))
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static String placeholder(String text, String command, double price) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%price%", String.valueOf(price)
                .replace("%command%", command)
                .toLowerCase(Locale.ROOT));
        return finalText;
    }

    public static String placeholder(String text, double buyPrice, double sellPrice) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%buyprice%", String.valueOf(buyPrice))
                .replace("%sellprice%", String.valueOf(sellPrice))
                .replace("_", " ")
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static String placeholder(String text, Player player) {
        String finalText = colorCode(text);
        finalText = finalText.replace("%playername%", player.getDisplayName())
                .toLowerCase(Locale.ROOT);
        return finalText;
    }

    public static String colorCode(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
