package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains(Utils.getMessage("binName", true).replaceAll("&", "§"))) return;
        if (event.getSlot() == -999) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory().equals(player.getInventory())) return;
        if (event.getSlot() > 44) event.setCancelled(true);

        //上一页
        if (event.getSlot() == 46) {
            PageGui pageGui = BinGui.pageGuiMap.get(player.getUniqueId());
            if (pageGui.hasPreviousPage()) {
                pageGui.goPreviousPage();
                pageGui.openGUI(player);
            }else {
                player.sendMessage(Utils.getMessage("noPreviousPage"));
            }
            return;
        }
        //下一页
        if (event.getSlot() == 52) {
            PageGui pageGui = BinGui.pageGuiMap.get(player.getUniqueId());
            if (pageGui.hasNextPage()) {
                pageGui.goNextPage();
                pageGui.openGUI(player);
            }else {
                player.sendMessage(Utils.getMessage("noNextPage"));
            }
        }
    }

    @EventHandler
    public void disableGui(InventoryCloseEvent event) {
        if (!event.getView().getTitle().contains(Utils.getMessage("binName", true).replaceAll("&", "§"))) return;
        Player player = (Player) event.getPlayer();
        BinGui.pageGuiMap.remove(player.getUniqueId());
    }
}
