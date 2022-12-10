package cn.konfan.clearentity.listener;

import cn.konfan.clearentity.ClearEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class FarmProtectListener implements Listener {

    private final Material soilMaterial = Material.getMaterial("SOIL") != null ? Material.getMaterial("SOIL") : Material.LEGACY_SOIL;
    private final List<String> worldList = ClearEntity.getInstance().getConfig().getStringList("EntityManager.farm-protect-worlds");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ClearEntity.getInstance().getConfig().getBoolean("EntityManager.enablePlayerFarmProtect")) return;
        if (!worldList.contains(event.getPlayer().getWorld().getName())) return;
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == soilMaterial) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (!ClearEntity.getInstance().getConfig().getBoolean("EntityManager.enableEntityFarmProtect")) return;
        if (!worldList.contains(event.getEntity().getWorld().getName())) return;
        if (!(event.getEntity() instanceof Player)) {
            if (event.getBlock().getType() == soilMaterial) {
                event.setCancelled(true);
            }
        }
    }
}
