package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.utils.ItemStackFactory;
import cn.konfan.clearentity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BinGui {
    public static List<Inventory> inventoryList = new ArrayList<>();
    public static Map<UUID, PageGui> pageGuiMap = new HashMap<>();
    public static boolean clear = false;

    static {
        init();
    }

    public static void init() {
        clear = false;
        closeAllGui();
        inventoryList.clear();
        //初始化箱子
        for (int i = 0; i < Utils.getConfig().getInt("Bin.Page"); i++) {
            inventoryList.add(Bukkit.createInventory(null, 6 * 9, Utils.getMessage("binName", true) + " - " + (i + 1) + Utils.getMessage("page", true)));
        }

        //版本兼容
        Material pane = Material.getMaterial("STAINED_GLASS_PANE");
        ItemStack itemStack = null;
        if (pane == null) {
            itemStack = new ItemStackFactory(Material.GRAY_STAINED_GLASS_PANE).toItemStack();
        } else {
            itemStack = new ItemStackFactory(pane).toItemStack();
        }
        for (Inventory itemStacks : inventoryList) {
            //站位
            for (int i = 45; i < 54; i++) {
                itemStacks.setItem(i, itemStack);
            }
            //翻页按钮
            itemStacks.setItem(46, new ItemStackFactory(Material.PAPER).setDisplayName(Utils.getMessage("previousPage", true)).toItemStack());
            itemStacks.setItem(52, new ItemStackFactory(Material.PAPER).setDisplayName(Utils.getMessage("nextPage", true)).toItemStack());

        }
    }

    public static boolean addItem(ItemStack itemStack) {
        for (int i = 0; i < inventoryList.size(); i++) {
            HashMap<Integer, ItemStack> item = inventoryList.get(i).addItem(itemStack);
            if (item.isEmpty()) return true;
        }
        return false;
    }

    public static void closeAllGui() {
        Set<UUID> uuids = pageGuiMap.keySet();
        for (UUID uuid : uuids) {
            try {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).closeInventory();
            } catch (NullPointerException ignore) {
                //
            }
        }
        pageGuiMap.clear();
    }

}
