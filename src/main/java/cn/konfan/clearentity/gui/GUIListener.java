package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.ClearEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    BinGui binGui = ClearEntity.binGui;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(InventoryClickEvent event){
        binGui.rawInventoryClickEvent(event);
    }
}
