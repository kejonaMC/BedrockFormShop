package dev.kejona.bedrockformshop.handlers;

import dev.kejona.bedrockformshop.BedrockFormShop;
import dev.kejona.bedrockformshop.config.ConfigurationHandler;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class PriceProvider {

    private final ConfigurationHandler SECTION = BedrockFormShop.getInstance().getSECTION();
    private final String menuID;
    private final String buttonID;

    public PriceProvider(String menuID, String buttonID) {
        this.menuID = menuID;
        this.buttonID = buttonID;
    }
    /**
     * Buy price setter.
     */
    @Nullable
    public BigDecimal buyPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("buy-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("buy-price"));
        } else {
            return null;
        }
    }
    /**
     * Sell price setter.
     */
    @Nullable
    public BigDecimal sellPrice() {
        if (SECTION.getButtonData(menuID, buttonID).isSet("sell-price")) {
            return BigDecimal.valueOf(SECTION.getButtonData(menuID, buttonID).getDouble("sell-price"));
        } else {
            return null;
        }
    }
}
