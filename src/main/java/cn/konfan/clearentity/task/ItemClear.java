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

public class ItemClear {


    public int run() {

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

            if (itemRules) {
                entity.remove();
                num.getAndIncrement();
            } else {
                return;
            }


            /**
             * Bin Config
             */
            if (!ClearEntity.getInstance().getConfig().getBoolean("EntityManager.Bin.enable")) {
                return;
            }
            Bin.addItem(((Item) entity).getItemStack());

        }));
        return Integer.parseInt(String.valueOf(num));
    }
}
