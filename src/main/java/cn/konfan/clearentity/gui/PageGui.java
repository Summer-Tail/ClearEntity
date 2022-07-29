package cn.konfan.clearentity.gui;

import org.bukkit.entity.Player;

public class PageGui {
    private int page = 1;

    public boolean hasPreviousPage() {
        return page > 1;
    }

    public boolean hasNextPage() {
        return page < BinGui.inventoryList.size();
    }

    public void goPreviousPage() {
        page--;
    }

    public void goNextPage() {
        page++;
    }

    public int getPage() {
        return page - 1;
    }


    public void openGUI(Player player) {
        player.openInventory(BinGui.inventoryList.get(page - 1));
        BinGui.pageGuiMap.put(player.getUniqueId(),this);
    }
}
