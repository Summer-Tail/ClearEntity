package cn.konfan.clearentity.utils;

import cn.konfan.clearentity.ClearEntity;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Method;

public class Utils {
    private static YamlConfiguration messagesConfig;

    static {
        File file = new File(ClearEntity.plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static void reloadMessage() {
        File file = new File(ClearEntity.plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }


    /**
     * 将 文本 中颜色字符从 & 转换为 §
     *
     * @param text 文本
     * @return 转换后文本
     */
    public static String getColorText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * 配置文件
     *
     * @return 配置文件
     */
    public static FileConfiguration getConfig() {
        return ClearEntity.plugin.getConfig();
    }


    public static String getMessage(String path, boolean notPrefix) {
        return !notPrefix ? messagesConfig.getString("prefix") + getColorText(messagesConfig.getString(path)) : getMessage(path);
    }

    public static String getMessage(String path) {
        return messagesConfig.getString("prefix") + getColorText(messagesConfig.getString(path));
    }

    /**
     * 获取该实体的 注册id
     *
     * @param entity 实体
     * @return 注册id 未知返回 ""
     */
    public static String getSaveID(Entity entity) {

        if (entity instanceof Player) {
            return "minecraft:player";
        }

        try {

            Object craftEntity = Class.forName("org.bukkit.craftbukkit." + ClearEntity.version + ".entity.CraftEntity").cast(entity);
            Method getHandle = craftEntity.getClass().getMethod("getHandle");
            Object nmsEntity = getHandle.invoke(craftEntity);

            Method getSaveID = nmsEntity.getClass().getMethod(ClearEntity.method);
            String saveId = (String) getSaveID.invoke(nmsEntity);
            return saveId == null ? "" : saveId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static BaseComponent jsonText(String text, HoverEvent hoverEvent, ClickEvent clickEvent) {
        BaseComponent url = new TextComponent(text);
        if (hoverEvent != null) {
            url.setHoverEvent(hoverEvent);
        }
        if (clickEvent != null) {
            url.setClickEvent(clickEvent);
        }
        return url;

    }

    public static boolean like(String text, String str) {
        if (str == null || "".equals(str)) {
            return false;
        }

        if (text.endsWith("*")) {
            return str.contains(text.replace(String.valueOf(text.charAt(text.length() - 1)), "").replaceAll("&", "§"));
        } else {
            return str.equals(text.replaceAll("&", "§"));
        }
    }


}
