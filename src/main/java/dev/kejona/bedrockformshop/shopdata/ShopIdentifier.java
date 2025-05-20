package dev.kejona.bedrockformshop.shopdata;

import java.math.BigDecimal;

public interface ShopIdentifier {

    String getShopName();

    void setShopName(String shopName);

    void setButtonName(String shopName);

    String getButtonName();

    BigDecimal getBuyPrice();

    BigDecimal getSellPrice();

    void setBuyPrice(BigDecimal buyPrice);

    void setSellPrice(BigDecimal sellPrice);
}
