package xyz.lvsheng.clearentity.utils;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.lvsheng.clearentity.ClearEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ConfigUtil {

    public static FileConfiguration getConfig(){
        return ClearEntity.plugins.getConfig();
    }


    public static boolean getMinecraft(){
        return getConfig().getBoolean("Minecraft");
    }

    public static int getMinEntity() {
        return getConfig().getInt("EntityNum.MinEntity");
    }

    public static int getMaxEntity() {
        return getConfig().getInt("EntityNum.MaxEntity");
    }

    public static int getClearTime() {
        int clearTime = getConfig().getInt("ClearTime");
        Integer maxTime = getMessageTime().get(getMessageTime().size() - 1);
        //小于等于60 会造成不间断清理
        return clearTime < 61 ? 3600 -maxTime  : clearTime -maxTime;
    }

    public static String getClearBefore() {
        return getConfig().getString("ClearMessages.ClearBefore");
    }

    public static String getClearComplete() {
        return getConfig().getString("ClearMessages.ClearComplete");
    }

    public static List<Integer> getMessageTime() {
        //按照大小排序
        return new ArrayList<>(new TreeSet<>(getConfig().getIntegerList("MessageTime")));
    }

    public static boolean isDropItems() {
        return getConfig().getBoolean("DropItems");
    }

    public static boolean isNam() {
        return getConfig().getBoolean("Nam");
    }

    public static List<String> getEntityWhite() {
        return getConfig().getStringList("EntityWhite");
    }

    public static List<String> getEntityBlack() {
        return getConfig().getStringList("EntityBlack");
    }

    public static List<String> getCustomWorld() {

        Set<String> set = new TreeSet<>();

        for (String key : getConfig().getKeys(true)) {

            String[] split = key.split("\\.");
            if (split.length == 2 && "CustomWorld".equals(split[0])) {
                set.add(split[1]);
            }
        }
        return new ArrayList<>(set);
    }
}
