/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.task;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.task.Clear.Rules;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemClear implements Runnable {

    @Override
    public void run() {
        /**
         * Clear Bin
         */
        if (Bin.clear) {
            Bin.clear = false;
            Bin.clearInv();
            Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Bin.binClear"));
        }


        AtomicInteger num = new AtomicInteger();
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            /**
             * Ignore other
             */
            if (!(entity instanceof Item)) {
                return;
            }

            /**
             * Clear
             */
            boolean itemRules = Rules.getItemRules(entity);

            if (itemRules) entity.remove();

            num.getAndIncrement();


            /**
             * Bin Config
             */
            if (!ClearEntity.getInstance().getConfig().getBoolean("EntityManager.Bin.enable")) {
                return;
            }
            boolean add = Bin.addItem(((Item) entity).getItemStack());
            if (add) {
                return;
            }
            Bin.clear = true;
        }));


        Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Clear.item").replaceAll("%COUNT%", num + ""));

    }
}
