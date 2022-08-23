package dev.kejona.bedrockformshop.shopdata;

import java.math.BigDecimal;

public class ShopData implements ShopIdentifier {

    private String menuID;
    private String buttonID;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;

    @Override
    public String getButtonID() {
        return buttonID;
    }

    @Override
    public String getMenuID() {
        return menuID;
    }

    @Override
    public BigDecimal getBuyPrice() {
        return  buyPrice;
    }

    @Override
    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    @Override
    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }

    @Override
    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    @Override
    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    @Override
    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

}
