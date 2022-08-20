/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

public class EntityNumScanner implements Runnable {
    public static int entity = 0;
    public static int item = 0;

    /**
     * Static init
     */
    static {
        upEntityNum();
    }

    public static void upEntityNum() {
        entity = 0;
        item = 0;

        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity1 -> {
            if (entity1 instanceof Item) {
                item++;
                return;
            }
            entity++;
        }));

    }

    @Override
    public void run() {
        upEntityNum();
    }
}
