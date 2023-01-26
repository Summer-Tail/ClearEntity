/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.task.Clear;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.nms.NMSUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rules {
    static FileConfiguration config = ClearEntity.getInstance().getConfig();

    public static boolean getRules(Entity entity) {
        return getRules(entity, false);
    }

    public static void getDebugRules(String entityName) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                String saveID = NMSUtils.getSaveID(entity);
                if (saveID.equals(entityName)) {
                    getRules(entity, true);
                    return;
                }
            }
        }
        ClearEntity.getInstance().getLogger().info("无法找到该实体，无法测试....");
    }

    public static boolean getRules(Entity entity, boolean debug) {
        /**
         * Reload config
         */
        config = ClearEntity.getInstance().getConfig();
        /**
         * Entity SaveID
         */
        String saveID = NMSUtils.getSaveID(entity);


        /**
         * Don't clear Player
         */
        if (entity instanceof Player) {
            return false;
        }


        /**
         * Rename Entity
         */
        if (saveID.startsWith("minecraft:") && !config.getBoolean("EntityManager.Rename") && !StringUtils.isBlank(entity.getCustomName())) {
            sendDebugInfo(debug, saveID, "NoClearRenameEntity", false);
            return false;
        }


        /**
         * Rules Selection
         */
        ConfigurationSection rules = config.getConfigurationSection("EntityManager.Rules.custom." + entity.getWorld().getName());
        rules = rules != null ? rules : config.getConfigurationSection("EntityManager.Rules");
        List<String> black = rules.getStringList("blacklist");
        List<String> white = rules.getStringList("whitelist");


        for (String list : white) {
            if (list.endsWith(":*") && saveID.contains(list.replaceAll(":\\*", ""))) {
                sendDebugInfo(debug, saveID, "Like White", false);
                return false;
            } else if (saveID.equalsIgnoreCase(list)) {
                sendDebugInfo(debug, saveID, "White", false);
                return false;
            }
        }

        for (String list : black) {
            if (list.endsWith(":*") && saveID.contains(list.replaceAll(":\\*", ""))) {
                sendDebugInfo(debug, saveID, "Like Black", true);
                return true;
            } else if (saveID.equalsIgnoreCase(list)) {
                sendDebugInfo(debug, saveID, "Black", true);
                return true;
            }
        }


        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            if ((white.contains("animals") && entity instanceof Animals) || white.contains("monster") && entity instanceof Monster) {
                sendDebugInfo(debug, saveID, "animals", false);
                return false;
            }
        }
        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            if (black.contains("monster") && entity instanceof Monster || black.contains("animals") && entity instanceof Animals) {
                sendDebugInfo(debug, saveID, "monster", true);
                return true;
            }
        }


        /**
         *  unknown
         */
        sendDebugInfo(debug, saveID, "unknown", false);
        return false;
    }

    public static boolean getItemRules(Entity item) {
        config = ClearEntity.getInstance().getConfig();

        ItemStack itemStack = ((Item) item).getItemStack();
        String itemName = "";
        List<String> itemLore = new ArrayList<>();
        String itemID = NMSUtils.getItemID(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemName = itemMeta.getDisplayName();
            itemLore = itemMeta.getLore();
        }


        ConfigurationSection section = config.getConfigurationSection("EntityManager.Rules.custom." + item.getWorld().getName() + ".item");
        ConfigurationSection itemConfig = section == null ? config.getConfigurationSection("EntityManager.Rules.item") : section;


        if (itemConfig == null || itemConfig.getKeys(false).size() == 1) {
            return true;
        }
        if (!itemConfig.getBoolean("enable")) {
            return false;
        }


        for (String key : itemConfig.getKeys(false)) {
            if ("enable".equals(key)) {
                continue;
            }

            String id = itemConfig.getString(key + ".id");
            String name = itemConfig.getString(key + ".name");
            String lore = itemConfig.getString(key + ".lore");

            //id判断
            if (StringUtils.isNotEmpty(id) && !id.equalsIgnoreCase(itemID)) {
                continue;
            }

            //name判断
            if (StringUtils.isNotEmpty(name)) {
                if (name.startsWith("*")) { //模糊匹配
                    if (!itemName.toLowerCase().contains(name.replace("*", "").toLowerCase())) {
                        continue;
                    }
                } else { //普通匹配
                    if (!name.equals(itemName)) {
                        continue;
                    }
                }
            }

            //lore判断
            if (StringUtils.isNotEmpty(lore)) {
                if (itemLore == null) continue;

                for (String s : itemLore) {
                    if (s.startsWith("*")) { //模糊匹配
                        if (s.toLowerCase().contains(lore.replace("*", "").toLowerCase())) {
                            return false;
                        }
                    } else { //普通匹配
                        if (s.equals(lore)) {
                            return false;
                        }
                    }
                }
            }
            return false;
        }

        return true;
    }

    private static void sendDebugInfo(boolean enable, String saveID, String rulesName, boolean result) {
        if (enable) {
            ClearEntity.getInstance().getLogger().info("----------------------------------------------------");
            ClearEntity.getInstance().getLogger().info("SaveID: " + saveID);
            ClearEntity.getInstance().getLogger().info("Matched: " + rulesName);
            ClearEntity.getInstance().getLogger().info("Result: " + result);
            ClearEntity.getInstance().getLogger().info("----------------------------------------------------");
        }
    }

}
