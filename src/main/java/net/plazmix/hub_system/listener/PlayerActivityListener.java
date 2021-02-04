package net.plazmix.hub_system.listener;

import lombok.RequiredArgsConstructor;
import net.plazmix.hub_system.config.HubSystemConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerActivityListener implements Listener {

    private final HubSystemConfig hubSystemConfig;

    @EventHandler(priority = EventPriority.LOW)
    private void on(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void on(PlayerInteractEvent event) {
        if (hubSystemConfig.isEditMode())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryClickEvent event) {
        if (hubSystemConfig.isEditMode())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryDragEvent event) {
        if (hubSystemConfig.isEditMode())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryMoveItemEvent event) {
        if (hubSystemConfig.isEditMode())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void on(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void on(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
            event.setCancelled(true);
        else {
            if (hubSystemConfig.getSpawnpoint() != null)
                event.getEntity().teleport(hubSystemConfig.getSpawnpoint());
        }
    }

    @EventHandler
    private void on(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }
}
