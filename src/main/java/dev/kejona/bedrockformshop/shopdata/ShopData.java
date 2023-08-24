package dev.kejona.bedrockformshop.shopdata;

import java.math.BigDecimal;

public class ShopData implements ShopIdentifier {

    private String shopName;
    private String buttonName;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;

    /**
     * Get the shop name.
     */
    @Override
    public String getShopName() {
        return shopName;
    }

    /**
     * Set the shop name.
     */
    @Override
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    /**
     * Set the button id.
     * @param buttonName Set the button id.
     */
    @Override
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * get the button name.
     */
    @Override
    public String getButtonName() {
        return buttonName;
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
