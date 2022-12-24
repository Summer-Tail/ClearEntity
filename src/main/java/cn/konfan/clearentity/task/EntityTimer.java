package cn.konfan.clearentity.task;

import cn.konfan.clearentity.ClearEntity;
import org.bukkit.configuration.file.FileConfiguration;

public class EntityTimer implements Runnable {
    private int time;
    private boolean maxClear = false;

    @Override
    public void run() {

        FileConfiguration config = ClearEntity.getInstance().getConfig();


        time++;
        int min = config.getInt("EntityManager.Limit.min");
        int max = config.getInt("EntityManager.Limit.max");
        if ((EntityNumScanner.entity > max) && max != -1 && !maxClear) {
            maxClear = true;
            EntityClear.clearStart();
            return;
        }
        if ((EntityNumScanner.entity < min) && min != -1) {
            return;
        }
        if (time != config.getInt("EntityManager.Time")) {
            return;
        }
        EntityClear.clearStart();
        maxClear = false;
        time = 0;
    }
}
