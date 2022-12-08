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
import org.bukkit.Chunk;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class EntityClear implements Runnable {

    public static int clearNum = 0;
    public static int index = 0;
    public static List<Chunk> chunks = new ArrayList<>();


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

        //Load Chunks
        Bukkit.getWorlds().forEach(world -> {
            chunks.addAll(Arrays.asList(world.getLoadedChunks()));
        });


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

        System.out.println("载入区块共计: " + Collections.synchronizedList(EntityClear.chunks).size() + " | 正在处理: " + index);
        int size = Math.min(index, Collections.synchronizedList(EntityClear.chunks).size());
        for (int i = index; i < size; i++) {

            Entity[] entities = Collections.synchronizedList(EntityClear.chunks).get(i).getEntities();
            for (Entity entity : entities) {
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
                clearNum++;
            }

        }


        //continue
        if (index < Collections.synchronizedList(EntityClear.chunks).size()) {
            index = index + ClearEntity.getInstance().getConfig().getInt("EntityManager.Limit.loadChunk");
            Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), new EntityClear(), 2L);
            return;
        }




        /**
         * Send completeMessage
         */
        Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Clear.clear").replaceAll("%COUNT%", "" + clearNum));

        /**
         * Send binMaxMessage
         */
        if (Bin.clear) {
            Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Bin.binClear"));
        }
        index = 0;
        clearNum = 0;
    }
}
