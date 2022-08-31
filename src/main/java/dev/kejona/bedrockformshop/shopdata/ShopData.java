package dev.kejona.bedrockformshop.shopdata;

import java.math.BigDecimal;

public class ShopData implements ShopIdentifier {

    private String menuID;
    private String buttonID;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;

    /**
     * Get the button id.
     */
    @Override
    public String getButtonID() {
        return buttonID;
    }
    /**
     * Get the menu id.
     */
    @Override
    public String getMenuID() {
        return menuID;
    }
    /**
     * Get the buy price of an item.
     */
    @Override
    public BigDecimal getBuyPrice() {
        return  buyPrice;
    }
    /**
     * Get the sell price of an item.
     */
    @Override
    public BigDecimal getSellPrice() {
        return sellPrice;
    }
    /**
     * Set the button id.
     * @param buttonID Set the button id.
     */
    @Override
    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }
    /**
     * Set the menu id.
     * @param menuID Set the menu id.
     */
    @Override
    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
    /**
     * Set the buy price of an item.
     * @param buyPrice set the buy price of an item
     */
    @Override
    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }
    /**
     * Set the sell price of an item.
     * @param sellPrice set the sell price of an item
     */
    @Override
    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

}
