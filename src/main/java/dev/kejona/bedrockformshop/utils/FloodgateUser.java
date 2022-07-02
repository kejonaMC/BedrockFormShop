package dev.kejona.bedrockformshop.utils;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateUser {
    /**
     * Determines if a player is a bedrock player
     * @param uuid the UUID to determine
     * @return true if the player is from floodgate
     */
    public static boolean isFloodgatePlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
