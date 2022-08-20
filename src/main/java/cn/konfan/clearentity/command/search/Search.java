package cn.konfan.clearentity.command.search;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.nms.NMSUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Search {

    /**
     * 搜索区块实体
     *
     * @param id    指定实体所在区块
     * @return      区块坐标/实体数量
     */
    public static Map<String, Integer> searchChunk(String id) {
        Map<String, Integer> map = new HashMap<>();
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (StringUtils.isNotEmpty(id) && !NMSUtils.getSaveID(entity).equals(id)) {
                return;
            }
            Chunk chunk = entity.getLocation().getChunk();
            String location = "[" + chunk.getX() + "," + chunk.getZ() + "]";
            map.put(location, map.get(location) == null ? 1 : map.get(location) + 1);
        }));

        return map;
    }

    /**
     * 搜索全部世界所存在的实体
     *
     * @param id    指定实体
     * @return      实体名称/实体数量
     */
    public static Map<String, Integer> searchEntity(String id) {
        Map<String, Integer> entityMap = new HashMap<>();
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (StringUtils.isNotEmpty(id) && !NMSUtils.getSaveID(entity).equals(id)) {
                return;
            }
            String saveID = NMSUtils.getSaveID(entity);
            entityMap.put(saveID, entityMap.get(saveID) == null ? 1 : entityMap.get(saveID) + 1);
        }));
        return entityMap;
    }

    /**
     * 发送搜索到的实体信息
     *
     * @param sender 接收人
     * @param map    信息
     * @param min    最小数量
     */
    public static void sendMessage(CommandSender sender, Map<String, Integer> map, int min) {
        sender.sendMessage(ClearEntity.convertColor("&b===================="));
        AtomicInteger num = new AtomicInteger();
        map.keySet().forEach(key -> {
            if (map.get(key) >= min) {
                sender.sendMessage(ClearEntity.convertColor("&a" + key + " &r|&b " + map.get(key)));
                num.getAndIncrement();
            }
        });
        sender.sendMessage(ClearEntity.convertColor("&b共扫描到：&c" + num + " &b项"));
    }
}
