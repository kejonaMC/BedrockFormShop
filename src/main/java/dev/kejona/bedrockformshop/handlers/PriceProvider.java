package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import dev.kejona.bedrockformshop.logger.Logger;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashMap;

public class PriceProvider {

    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final String menuID;
    private final String buttonID;
    private final HashMap<Boolean, String> plugins = BedrockFormShop.getInstance().getHookedPlugins();

    private final Logger logger = Logger.getLogger();

    public PriceProvider(String menuID, String buttonID) {
        this.menuID = menuID;
        this.buttonID = buttonID;
    }

    public BigDecimal buyPrice(Material material) {
        try {
            if (plugins.containsKey(true)) {
                // Get plugins name.
                String pluginName = plugins.get(true);
                // GuiShop prices
                if (pluginName.equals("GuiShop")) {
                    // guishop stuff
                }
                // EconomyShopGUI prices
                if (pluginName.equals("EconomyShopGUI")) {
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemBuyPrice(new ItemStack(material)));

                }
            }
            // If somehow api's returned a null we check our own price list if price is present.
        } catch (Exception ignored) {}

        logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + " If a price is present in BedrockFormShop config that price will be used.");
        // Always default back to our price list
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("buy-price"));
        }

        return null;
    }

    public BigDecimal sellPrice(Material material) {
        try {
            if (plugins.containsKey(true)) {
                // Get plugins name.
                String pluginName = plugins.get(true);
                // GuiShop prices
                if (pluginName.equals("GuiShop")) {
                }
                // EconomyShopGUI prices.
                if (pluginName.equals("EconomyShopGUI")) {
                    return BigDecimal.valueOf(EconomyShopGUIHook.getItemSellPrice(new ItemStack(material)));

                }
            }
            // If somehow api's returned a null we check our own price list if price is present.
        } catch (Exception ignored) {}

        logger.debug("Was unable to get a price of (" + material.name() + ") from " + plugins.get(true) + ". If a price is present in BedrockFormShop config that price will be used.");
        // Always default back to our price list
        if (SECTION.getButtonData(menuID, buttonID).isSet("sell-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        }

        return null;
    }
}
