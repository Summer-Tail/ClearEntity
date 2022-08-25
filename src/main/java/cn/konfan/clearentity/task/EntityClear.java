/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.task;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.task.Clear.Rules;
import cn.konfan.clearentity.utils.BossBarUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Item;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class EntityClear implements Runnable {
    public static void clearStart() {


        /**
         * Sort
         */
        List<Integer> sendTime = ClearEntity.getInstance().getConfig().getIntegerList("EntityManager.Message.time");
        Collections.sort(sendTime);

        /**
         * Send beforeMessage
         */
        String before = LanguageConfig.getString("Clear.before");
        int maxTime = sendTime.get(sendTime.size() - 1);
        Bukkit.getScheduler().runTask(ClearEntity.getInstance(), () -> Bukkit.getServer().broadcastMessage(before.replaceAll("%TIME%", "" + maxTime)));
        for (int i = sendTime.size() - 2; i >= 0; i--) {
            int time = sendTime.get(i);
            Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), () -> Bukkit.getServer().broadcastMessage(before.replaceAll("%TIME%", "" + time)), (maxTime - sendTime.get(i)) * 20L);
        }

        /**
         * Run clearEntity
         */
        Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), new EntityClear(), sendTime.get(sendTime.size() - 1) * 20);

        Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), new ItemClear(), sendTime.get(sendTime.size() - 1) * 20);

        /**
         * Send bossbar
         */
        if (ClearEntity.getInstance().getConfig().getBoolean("EntityManager.Message.bossBar")) {
            new BossBarUtils().sendBossBar("Title", BarColor.RED, BarStyle.SOLID, sendTime.get(sendTime.size() - 1) + 1);
        }
    }


    @Override
    public void run() {

        /**
         * Clear entities
         */
        AtomicInteger num = new AtomicInteger();
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {

            if (entity instanceof Item) {
                return;
            }

            if (!Rules.getRules(entity)) {
                return;
            }
            entity.remove();
            if (!entity.isDead()) {
                return;
            }
            num.getAndIncrement();

        }));

        /**
         * Send completeMessage
         */
        Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Clear.clear").replaceAll("%COUNT%", "" + num));

        /**
         * Send binMaxMessage
         */
        if (Bin.clear) {
            Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Bin.binClear"));
        }

    }
}
