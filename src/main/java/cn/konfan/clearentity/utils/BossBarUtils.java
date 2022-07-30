package cn.konfan.clearentity.utils;

import cn.konfan.clearentity.ClearEntity;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarUtils implements Runnable {
    private BossBar bossBar = null;
    private double time;
    private int seconds;

    public BossBar sendBossBar(String title, BarColor barColor, BarStyle style, int time) {
        bossBar = Bukkit.createBossBar(title, barColor, style);
        bossBar.setProgress(1.00);
        seconds = time;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
        }
        this.repeat(time, 20, this, bossBar::removeAll);
        this.time = (Double.parseDouble(time + "") / 100) * 0.01;

        return bossBar;
    }


    @Override
    public void run() {
        seconds--;
        double progress = bossBar.getProgress();
        bossBar.setProgress(progress >= time ? progress - time : 0);
        bossBar.setTitle(Utils.getColorText(Utils.getMessage("BossBar").replaceAll("%COUNT%", seconds + "")));
    }


    private void repeat(int repetitions, long interval, Runnable task, Runnable onComplete) {
        new BukkitRunnable() {
            private int index;

            @Override
            public void run() {
                index++;
                if (this.index >= repetitions) {
                    this.cancel();
                    if (onComplete == null) {
                        return;
                    }

                    onComplete.run();
                    return;
                }

                task.run();
            }
        }.runTaskTimer(ClearEntity.plugin, 0L, interval);
    }
}