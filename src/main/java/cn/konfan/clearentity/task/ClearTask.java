package cn.konfan.clearentity.task;


import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.clear.EntityClear;
import cn.konfan.clearentity.utils.Utils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ClearTask implements Runnable {
    private Integer time = 0;

    @Override
    public void run() {
        ++time;

        //实体过少,不执行清理任务
        if (Utils.getConfig().getInt("Clear.Min") > 0 && EntityNumTask.creature < Utils.getConfig().getInt("Clear.Min")) {
            return;
        }


        //时间判断
        if (time >= Utils.getConfig().getInt("Time")) {
            clearStart();
            return;
        }

        //实体数量判断
        if (Utils.getConfig().getInt("Clear.Max") > 0 && EntityNumTask.creature >= Utils.getConfig().getInt("Clear.Max")) {
            clearStart();
        }


    }

    public static void clearStart() {

        //重新排序,便于读取
        List<Integer> timeList = new ArrayList<>(new TreeSet<Integer>(Utils.getConfig().getIntegerList("Message.Time")));

        //发送提示
        for (int i = timeList.size() - 1; i >= 0; i--) {

            int time = timeList.get(timeList.size() - 1) - timeList.get(i);
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ClearEntity.plugin,
                    new Thread(() -> Bukkit.getServer().broadcastMessage(Utils.getColorText(Utils.getConfig().getString("Message.Before").replaceAll("%TIME%", "" + timeList.get(finalI))))), time * 20L);
        }

        //执行清理
        Bukkit.getScheduler().scheduleSyncDelayedTask(ClearEntity.plugin, new EntityClear(), timeList.get(timeList.size() - 1) * 20L);

        //停止当前Task 并重新启动一个
        Bukkit.getScheduler().cancelTask(ClearEntity.clearTask);
        ClearEntity.clearTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(ClearEntity.plugin, new ClearTask(), timeList.get(timeList.size() - 1), 20);
    }


}
