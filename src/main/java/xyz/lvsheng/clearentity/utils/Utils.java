package xyz.lvsheng.clearentity.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import xyz.lvsheng.clearentity.ClearEntity;


import java.lang.reflect.Method;
import java.util.List;


public class Utils {
    private static final String VERSION;
    private static String found = null;

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
    public static String colorMessage(String str) {
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
        if (entity instanceof Player){
            return "minecraft:player";
        }
        try {

            Object craftEntity = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftEntity").cast(entity);
            Method getHandle = craftEntity.getClass().getMethod("getHandle");
            Object nmsEntity = getHandle.invoke(craftEntity);


            Method getSaveID = null;
            try {
                getSaveID = nmsEntity.getClass().getMethod(found == null ? "getSaveID" : found);
                found = "getSaveID";
            } catch (NoSuchMethodError e) {
                //1.18方法名变动
                getSaveID = nmsEntity.getClass().getMethod("bk");
                found = "bk";
            }

            return (String) getSaveID.invoke(nmsEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                Bukkit.getConsoleSender().sendMessage(colorMessage("&a[ClearEntity] &e获取服务器版本成功 " + version + i));
                return version + i;
            } catch (Exception ignored) {
                //忽略异常
            }
        }


        Bukkit.getConsoleSender().sendMessage(colorMessage("&4[ClearEntity] &c严重错误,无法获取服务器版本,插件将无法正常运行,请尝试更新插件!!!"));
        ClearEntity.plugins.onDisable();
        return null;
    }


}
