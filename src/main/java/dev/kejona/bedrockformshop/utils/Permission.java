package dev.kejona.bedrockformshop.utils;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.entity.Player;

import java.util.UUID;

public enum Permission {
    // Enums can be unused since we only check for valueof.
    SHOP("bedrockformshop.openshop"),
    PLAYER("bedrockformshop.playermenu"),
    VIP("bedrockformshop.vipmenu"),
    ADMIN("bedrockformshop.adminmenu"),
    RELOAD("bedrockformshop.reload");

    private final String key;

    Permission(String key) {
        this.key = key;
    }
    // Check if player has permission.
    public boolean checkPermission(UUID uuid) {
        Player p = BedrockFormShop.getInstance().getServer().getPlayer(uuid);
        if (p != null) {
            return p.hasPermission(this.getKey());
        }
        return false;
    }

    public String getKey() {
        return key;
    }
}