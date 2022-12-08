package cn.konfan.clearentity.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FarmProtectListener implements Listener {

    private final Material soilMaterial = Material.getMaterial("SOIL") != null ? Material.getMaterial("SOIL") : Material.LEGACY_SOIL;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == soilMaterial) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            if (event.getBlock().getType() == soilMaterial) {
                event.setCancelled(true);
            }
        }
    }
}
