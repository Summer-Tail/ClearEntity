package cn.konfan.clearentity.task;


import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.clear.EntityClear;
import cn.konfan.clearentity.utils.BossBarUtils;
import cn.konfan.clearentity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ClearTask implements Runnable {
    private int time;
    private boolean maxClear = false;
    private final FileConfiguration config = ClearEntity.plugin.getConfig();
    @Override
    public void run() {
        time++;
        int min = config.getInt("Clear.Min");
        int max = config.getInt("Clear.Max");
        if ((EntityNumTask.entity > max) && max != -1 && !maxClear) {
            maxClear = true;
            EntityClear.clearStart();
            return;
        }
        if ((EntityNumTask.entity > min) && min != -1) return;
        if (time != config.getInt("Message.Time")) return;
        EntityClear.clearStart();
        maxClear = false;
        time = 0;
    }



}
