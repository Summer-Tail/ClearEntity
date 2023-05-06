/**
 * ClearEntity - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.utils.ItemStackFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Bin {
    public static Boolean clear = false;
    private static final HashMap<UUID, Integer> pages = new HashMap<>();
    private static final List<Inventory> invs = new ArrayList<>();
    private static Integer maxPage = ClearEntity.getInstance().getConfig().getInt("EntityManager.Bin.page");

    static {
        initGui();
    }

    private static void initGui() {
        invs.clear();
        maxPage = ClearEntity.getInstance().getConfig().getInt("EntityManager.Bin.page");
        for (int i = 0; i < maxPage; i++) {
            invs.add(Bukkit.createInventory(null, 54, LanguageConfig.getString("Bin.binTitle").replaceAll("%PAGE%", i + 1 + "")));
        }
    }

    public static HashMap<UUID, Integer> getPages() {
        return pages;
    }

    public static Integer getPages(Player player) {
        Integer page = getPages().get(player.getUniqueId());
        return page == null ? 0 : page;
    }

    public static void open(Player player, Integer page) {
        resetGui(page);
        getPages().put(player.getUniqueId(), 0);
        player.openInventory(invs.get(page));
    }

    public static void addItem(ItemStack item) {
        for (Inventory inv : invs) {
            HashMap<Integer, ItemStack> itemMap = inv.addItem(item);
            if (itemMap.size() == 0) return;
        }
    }

    public static Boolean goNextPage(Player player) {
        Integer page = getPages(player);
        if (page + 1 < maxPage) {
            open(player, ++page);
            getPages().put(player.getUniqueId(), page);
            return true;
        }
        return false;
    }

    public static Boolean goPreviousPage(Player player) {
        Integer page = getPages(player);
        if (page + 1 > 1) {
            open(player, --page);
            getPages().put(player.getUniqueId(), page);
            return true;
        }
        return false;
    }


    public static Boolean isOpenBin(Inventory inventory) {
        for (Inventory inv : invs) {
            if (inv.equals(inventory)) {
                return true;
            }
        }
        return false;
    }

    public static void closeAllGui() {
        getPages().forEach((uuid, integer) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }
        });
    }

    public static void clearInv() {
        Bin.closeAllGui();
        initGui();
    }

    /**
     * 重置Gui
     *
     * @param page 页
     */
    private static void resetGui(int page) {
        String previousPage = LanguageConfig.getString("Bin.previousPage");
        String nextPage = LanguageConfig.getString("Bin.nextPage");

        //清除原有翻页与占位按钮
        for (ItemStack itemStack : invs.get(page)) {
            if (itemStack == null) {
                continue;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                continue;
            }
            if (itemMeta.getDisplayName().equals(previousPage) | itemMeta.getDisplayName().equals(nextPage) | itemMeta.getDisplayName().equals("§aclearentity")) {
                itemStack.setAmount(0);
            }

        }

        //重新创建翻页占位按钮
        //版本兼容
        Material material = Material.getMaterial("STAINED_GLASS_PANE") != null ?
                Material.getMaterial("STAINED_GLASS_PANE") : Material.getMaterial("GRAY_STAINED_GLASS_PANE");
        //占位
        for (int o = 45; o < 54; o++) {
            invs.get(page).setItem(o, new ItemStackFactory(material).setDisplayName("§aclearentity").toItemStack());
        }
        //翻页按钮
        invs.get(page).setItem(46, new ItemStackFactory(Material.PAPER).setDisplayName(LanguageConfig.getString("Bin.previousPage")).toItemStack());
        invs.get(page).setItem(52, new ItemStackFactory(Material.PAPER).setDisplayName(LanguageConfig.getString("Bin.nextPage")).toItemStack());
    }

}
