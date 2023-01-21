/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.listener;

import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiListener implements Listener {

    @EventHandler
    public void onBin(InventoryClickEvent event) {

        String previousPage = LanguageConfig.getString("Bin.previousPage");
        String nextPage = LanguageConfig.getString("Bin.nextPage");
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!Bin.isOpenBin(event.getClickedInventory())) return;
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (itemMeta.getDisplayName().equals(previousPage) | itemMeta.getDisplayName().equals(nextPage) | itemMeta.getDisplayName().equals("§aclearentity")) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getSlot() == 46) {
            if (!Bin.goPreviousPage(player)) {
                player.sendMessage(LanguageConfig.getString("Bin.notPreviousPage"));
            }
            return;
        }
        if (event.getSlot() == 52) {
            if (!Bin.goNextPage(player)) {
                player.sendMessage(LanguageConfig.getString("Bin.notNextPage"));
            }
        }

    }

    @EventHandler
    public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
        Bin.getPages().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Bin.getPages().remove(event.getPlayer().getUniqueId());
    }

}
