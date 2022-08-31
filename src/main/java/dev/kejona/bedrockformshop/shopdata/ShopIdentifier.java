package dev.kejona.bedrockformshop.shopdata;

import java.math.BigDecimal;

public interface ShopIdentifier {

    String getButtonID();

    String getMenuID();

    BigDecimal getBuyPrice();

    BigDecimal getSellPrice();

    void setButtonID(String buttonID);

    void setMenuID(String menuID);

    void setBuyPrice(BigDecimal buyPrice);

    void setSellPrice(BigDecimal sellPrice);
}
