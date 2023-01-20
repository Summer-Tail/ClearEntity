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
         * Send beforeMessage
         */
        List<Integer> sendTime = ClearEntity.getInstance().getConfig().getIntegerList("EntityManager.Message.time");
        ClearEntity.SendCountdownUtil(sendTime, LanguageConfig.getString("Clear.before"), "%TIME%");

        /**
         * Run clearEntity
         */
        Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), new EntityClear(), sendTime.get(sendTime.size() - 1) * 20);
        /**
         * Send bossbar
         */
        if (ClearEntity.getInstance().getConfig().getBoolean("EntityManager.Message.bossBar")) {
            new BossBarUtils().sendBossBar("Title", BarColor.RED, BarStyle.SOLID, sendTime.get(sendTime.size() - 1) + 1);
        }
    }


    @Override
    public void run() {

        if (chunks.size() == 0) {
            Bukkit.getWorlds().forEach(world -> {
                chunks.addAll(Arrays.asList(world.getLoadedChunks()));
            });
        }


        /**
         * Clear entities
         */


        int loadChunkNum = ClearEntity.getInstance().getConfig().getInt("EntityManager.Limit.loadChunk");
        int clearSize = Math.min(loadChunkNum, Collections.synchronizedList(EntityClear.chunks).size());
        int chunkSize = Collections.synchronizedList(EntityClear.chunks).size();

        for (int i = index; i < Math.min(chunkSize, clearSize + index); i++) {
            index = i;
            Entity[] entities = Collections.synchronizedList(EntityClear.chunks).get(i).getEntities();
            for (Entity entity : entities) {
                if (entity instanceof Item) {
                    continue;
                }

                if (!Rules.getRules(entity)) {
                    continue;
                }
                entity.remove();
                if (!entity.isDead()) {
                    continue;
                }
                clearNum++;
            }
        }

        //continue
        if (index < Collections.synchronizedList(EntityClear.chunks).size() - 1) {
            Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), new EntityClear(), 2L);
            return;
        }


        int itemNum = new ItemClear().run();


        /**
         * Send completeMessage
         */
        Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Clear.clear").replaceAll("%COUNT%", "" + clearNum).replaceAll("%ITEMCOUNT%", itemNum + ""));

        index = 0;
        clearNum = 0;
        Collections.synchronizedList(chunks).clear();
    }
}