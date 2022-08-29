package dev.kejona.bedrockformshop.listeners;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlacedSpawner implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.SPAWNER) {
            try {
                CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
                spawner.setSpawnedType(EntityType.valueOf(Objects.requireNonNull(item.getItemMeta()).getDisplayName()));
                spawner.update();
            } catch (Exception ignored) {
                // Ignore only happens spawner is not one bought in our shop
            }
        }
    }
}
