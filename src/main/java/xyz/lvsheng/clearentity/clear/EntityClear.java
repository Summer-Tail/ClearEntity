package xyz.lvsheng.clearentity.clear;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import xyz.lvsheng.clearentity.utils.ConfigUtil;
import xyz.lvsheng.clearentity.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class EntityClear implements Runnable, Callable<Integer> {
    public static List<Integer> ignore = new ArrayList<>();

    @Override
    public void run() {
        Bukkit.getServer().broadcastMessage(Utils.colorMessage
                (ConfigUtil.getClearComplete().replace("%COUNT%", "" + this.clear())));
        //清理缓存
        ignore.clear();
    }


    /**
     * 该实体是否允许被清除
     *
     * @param entity 实体
     * @return 是否允许
     */
    public static boolean getRules(Entity entity) {


        //忽略玩家
        if (entity instanceof Player) {
            return false;
        }

        //忽略可能正在追踪玩家的实体
        if (ignore.contains(entity.getEntityId())){
            return false;
        }


        //根据配置文件 忽略命名实体
        if (!ConfigUtil.isNam() && entity.getCustomName() != null) {
            return false;
        }

        List<String> black;
        List<String> white;
        String id = Utils.getID(entity);
        if (id == null) {
            return false;
        }


        //忽略非原版生物
        if (ConfigUtil.getMinecraft() && !id.startsWith("minecraft")) {
            return false;
        }


        //全局规则
        if (!Utils.equalsIgnoreCase(ConfigUtil.getCustomWorld(), entity.getWorld().

                getName())) {
            black = ConfigUtil.getEntityBlack();
            white = ConfigUtil.getEntityWhite();
        } else {
            //自定义规则
            black = ConfigUtil.getConfig().getStringList
                    ("CustomWorld." + entity.getWorld().getName() + ".EntityBlack");
            white = ConfigUtil.getConfig().getStringList
                    ("CustomWorld." + entity.getWorld().getName() + ".EntityWhite");
        }


        //清理掉落物
        if (ConfigUtil.isDropItems()) {
            black.add("minecraft:item");

            //识别物品名
            if (entity instanceof Item) {

                String itemName = ((Item) entity).getItemStack().getType().name();

                if (Utils.equalsIgnoreCase(white, "item:" + itemName)) {
                    return false;
                }

            }

        } else {
            black.remove("minecraft:item");
        }


        //白名单实体
        if (Utils.equalsIgnoreCase(white, id)) {
            return false;
        }


        //黑名单实体
        if (Utils.equalsIgnoreCase(black, id)) {
            return true;
        }


        //未配置实体
        if (Utils.equalsIgnoreCase(black, "monster")) {
            return entity instanceof Monster;
        }


        if (Utils.equalsIgnoreCase(black, "animals")) {
            return entity instanceof Animals;
        }


        return false;
    }


    /**
     * 实体清除
     *
     * @return 成功被清除的实体数量
     */
    private int clear() {
        int remove = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                if (!getRules(entity)) {
                    continue;
                }

                entity.remove();

                if (!entity.isDead()) {
                    continue;
                }

                remove++;
            }
        }
        return remove;
    }


    /**
     * 根据规则返回可以被清除的实体数量
     *
     * @return 实体数量
     */
    @Override
    public Integer call() {

        int entityNum = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                if (!EntityClear.getRules(entity)) {
                    continue;
                }

                entityNum++;
            }
        }
        return entityNum;
    }
}
