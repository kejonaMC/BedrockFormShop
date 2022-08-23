package dev.kejona.bedrockformshop.shopdata;

public class ShopData implements ShopIdentifier {

    private String menuID;
    private String buttonID;

    @Override
    public String getButtonID() {
        return buttonID;
    }

    @Override
    public String getMenuID() {
        return menuID;
    }

    @Override
    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }

    @Override
    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
