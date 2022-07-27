package cn.konfan.clearentity.gui;

import cn.konfan.clearentity.utils.ItemStackFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BinGui {
    static List<Inventory> inventoryList = new ArrayList<>();
    private static int page = 1;


    public BinGui() {
        for (int i = 4; i > 0; i--) {
            inventoryList.add(Bukkit.createInventory(null, 6 * 9, "公共垃圾箱" + " - " + (i + 1) + " 页"));
        }
        for (int size = inventoryList.size() - 1; size > 0; size--) {
            inventoryList.get(size).setItem(46, new ItemStackFactory(Material.PAPER).setDisplayName("上一页").toItemStack());
            inventoryList.get(size).setItem(52, new ItemStackFactory(Material.PAPER).setDisplayName("下一页").toItemStack());
        }
    }


    public boolean hasPreviousPage() {
        return page > 1;
    }

    public boolean hasNextPage() {
        return page > inventoryList.size();
    }

    public void goPreviousPage() {
        page--;
    }

    public void goNextPage() {
        page++;
    }

    public static int getPage() {
        return page - 1;
    }

    public void setItem(int page, int slot, ItemStack itemStack) {
        inventoryList.get(getPage()).setItem(slot, itemStack);
    }


    public void addItem(int page, ItemStack itemStack) {
        inventoryList.get(getPage()).addItem(itemStack);
    }

    public ItemStack getItem(int page, int slot) {
        return inventoryList.get(getPage()).getItem(slot);
    }

    public void removeItem(int page, int slot) {
        ItemStack item = inventoryList.get(page).getItem(slot);
        if (item == null) {
            return;
        }
        this.removeItem(page, item);
    }

    public void removeItem(int page, ItemStack itemStack) {
        inventoryList.get(getPage()).remove(itemStack);
    }

    public static void openGUI(Player player) {
        player.openInventory(inventoryList.get(getPage()));
    }


    public void rawInventoryClickEvent(InventoryClickEvent event) {
        for (Inventory inventory : inventoryList) {
            if (inventory == event.getInventory()) {
                break;
            } else {
                return;
            }
        }




    }

}
