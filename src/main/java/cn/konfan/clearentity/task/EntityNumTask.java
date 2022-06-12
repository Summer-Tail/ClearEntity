package cn.konfan.clearentity.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class EntityNumTask implements Runnable {

    public static Integer creature = 0;
    public static Integer item = 0;

    @Override
    public void run() {
        creature = 0;
        item = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    item++;
                    continue;
                }
                creature++;
            }
        }


    }
}
