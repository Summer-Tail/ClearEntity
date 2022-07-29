package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.utils.ItemStackFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BinGui {
    public static List<Inventory> inventoryList = new ArrayList<>();
    public static Map<UUID, PageGui> pageGuiMap = new HashMap<>();

    static {
        //初始化箱子
        for (int i = 0; i < 4; i++) {
            inventoryList.add(Bukkit.createInventory(null, 6 * 9, "公共垃圾箱" + " - " + (i + 1) + " 页"));
        }

        //版本兼容
        Material pane = Material.getMaterial("STAINED_GLASS_PANE");
        ItemStack itemStack = null;
        if (pane == null) {
            itemStack = new ItemStackFactory(Material.GRAY_STAINED_GLASS_PANE).toItemStack();
        } else {
            itemStack = new ItemStack(pane, 0, (short) 0, (byte) 8);
        }
        for (Inventory itemStacks : inventoryList) {
            //站位
            for (int i = 45; i < 54; i++) {
                itemStacks.setItem(i, itemStack);
            }
            //翻页按钮
            itemStacks.setItem(46, new ItemStackFactory(Material.PAPER).setDisplayName("上一页").toItemStack());
            itemStacks.setItem(52, new ItemStackFactory(Material.PAPER).setDisplayName("下一页").toItemStack());
        }
    }

    public static void setItem(int page, int slot, ItemStack itemStack) {
        inventoryList.get(page).setItem(slot, itemStack);
    }


    public static void addItem(ItemStack itemStack) {
        for (Inventory itemStacks : inventoryList) {
            HashMap<Integer, ItemStack> item = itemStacks.addItem(itemStack);
            if (item.isEmpty()) break;
        }

    }

    public static ItemStack getItem(int page, int slot) {
        return inventoryList.get(page).getItem(slot);
    }

    public static void removeItem(int page, int slot) {
        ItemStack item = inventoryList.get(page).getItem(slot);
        if (item == null) {
            return;
        }
        removeItem(page, item);
    }

    public static void removeItem(int page, ItemStack itemStack) {
        inventoryList.get(page).remove(itemStack);
    }

    public void openGUI(Player player) {

    }
}
