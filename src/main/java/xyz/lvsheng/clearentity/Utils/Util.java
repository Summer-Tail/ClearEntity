package xyz.lvsheng.clearentity.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import xyz.lvsheng.clearentity.ClearEntity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class Util implements Callable<Integer> {
    private static final String VERSION;
    private static String FOUND;

    //获取服务器版本号
    static {
        VERSION = getVersion();
    }


    /**
     * 彩色文字
     *
     * @param str 文字
     * @return 文字
     */
    public static String ColorMessage(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }


    /**
     * 忽略大小写判断 list是否存在此字符串
     *
     * @param list 要查询的list
     * @param str  查询的字符串
     * @return 是否存在
     */
    public static boolean equalsIgnoreCase(List<String> list, String str) {
        for (String s : list) {
            if (s.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 返回实体ID
     *
     * @param entity 要查询的实体
     * @return 实体ID
     */
    public static String getID(Entity entity) {
        try {

            Object craft = Class.forName
                    ("org.bukkit.craftbukkit." + VERSION + "." + "entity.CraftEntity").cast(entity);
            Object nms = craft.getClass().getMethod("getHandle").invoke(craft);
            return (String) nms.getClass().getMethod(FOUND).invoke(nms);

        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }


    /**
     * 该实体是否允许被清除
     *
     * @param entity 实体
     * @return 是否允许
     */
    public static boolean getRules(Entity entity) {


        //忽略玩家
        if ("Player".equalsIgnoreCase(entity.getType().name())) {
            return false;
        }


        //根据配置文件 忽略命名实体
        if (!ClearEntity.configUtil.isNam()) {
            if (entity.getCustomName() != null) {
                return false;
            }
        }

        List<String> black;
        List<String> white;
        String id = getID(entity);
        if (Objects.equals(id, null)) {
            return false;
        }
        String[] split = id.split(":");


        //忽略非原版生物
        if (split.length != 0) {
            if (ClearEntity.configUtil.getMinecraft() && !"minecraft".equalsIgnoreCase(split[0])) {
                return false;
            }
        }


        //全局规则
        if (!equalsIgnoreCase(ClearEntity.configUtil.getCustomWorld(), entity.getWorld().getName())) {
            black = ClearEntity.configUtil.getEntityBlack();
            white = ClearEntity.configUtil.getEntityWhite();
        } else {
            //自定义规则
            black = ClearEntity.plugins.getConfig().getStringList
                    ("CustomWorld." + entity.getWorld().getName() + ".EntityBlack");
            white = ClearEntity.plugins.getConfig().getStringList
                    ("CustomWorld." + entity.getWorld().getName() + ".EntityWhite");
        }


        //清理掉落物
        if (ClearEntity.configUtil.isDropItems()) {
            black.add("minecraft:item");

            //识别物品名
            if (entity instanceof Item) {

                String itemName = ((Item) entity).getItemStack().getType().name();

                if (equalsIgnoreCase(white,"item:"+itemName)){
                    return false;
                }

            }

        } else {
            black.remove("minecraft:item");
        }


        //白名单实体
        if (equalsIgnoreCase(white, id)) {
            return false;
        }


        //黑名单实体
        if (equalsIgnoreCase(black, id)) {
            return true;
        }


        //未配置实体
        if (equalsIgnoreCase(black, "monster")) {
            return entity instanceof Monster;
        }


        if (equalsIgnoreCase(black, "animals")) {
            return entity instanceof Animals;
        }


        return false;
    }

    /**
     * 获取服务器版本号
     */
    private static String getVersion() {
        String[] split = Bukkit.getBukkitVersion().split("\\.");
        String version = "v" + split[0] + "_" + split[1].replaceAll("-R.*", "") + "_R";

        for (int i = 1; i < 10; i++) {
            try {
                Class.forName("org.bukkit.craftbukkit." + version + i + ".entity.CraftEntity");

                //高版本方法名不一致
                if (Integer.parseInt(split[1].replaceAll("-R.*", "")) > 17) {
                    FOUND = "bk";
                } else {
                    FOUND = "getSaveID";
                }

                Bukkit.getConsoleSender().sendMessage(ColorMessage("&a[ClearEntity] &e获取服务器版本成功 " + version + i));
                return version + i;
            } catch (Exception ignored) {
                //忽略异常
            }
        }


        Bukkit.getConsoleSender().sendMessage(ColorMessage("&4[ClearEntity] &c严重错误,无法获取服务器版本,插件将无法正常运行,请尝试更新插件!!!"));
        ClearEntity.plugins.onDisable();
        return null;
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
                if (getRules(entity)) {
                    entityNum++;
                }
            }
        }
        return entityNum;
    }
}
