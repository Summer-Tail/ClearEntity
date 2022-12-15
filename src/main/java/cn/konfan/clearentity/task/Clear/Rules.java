/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.task.Clear;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.nms.NMSUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.Objects;

public class Rules {
    static FileConfiguration config = ClearEntity.getInstance().getConfig();

    public static boolean getRules(Entity entity) {
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
            return false;
        }


        /**
         * Rules Selection
         */
        ConfigurationSection rules = config.getConfigurationSection("EntityManager.Rules.custom." + entity.getWorld().getName());
        rules = rules != null ? rules : config.getConfigurationSection("EntityManager.Rules");
        List<String> black = rules.getStringList("blacklist");
        List<String> white = rules.getStringList("whitelist");


        if (white.contains(saveID)) {
            return false;
        }
        if (black.contains(saveID)) {
            return true;
        }


        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            if ((white.contains("animals") && entity instanceof Animals) || white.contains("monster") && entity instanceof Monster) {
                return false;
            }
        }
        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            return (black.contains("monster") && entity instanceof Monster) || (black.contains("animals") && entity instanceof Animals);
        }


        /**
         *  unknown
         */
        return false;
    }

    public static boolean getItemRules(Entity item) {
        config = ClearEntity.getInstance().getConfig();

        ItemStack itemStack = ((Item) item).getItemStack();
        String itemName = Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        String itemID = NMSUtils.getItemID(itemStack);


        ConfigurationSection section = config.getConfigurationSection("EntityManager.Rules.custom." + item.getWorld().getName() + ".item");
        ConfigurationSection itemConfig = section == null ? config.getConfigurationSection("EntityManager.Rules.item") : section;


        if (itemConfig == null || itemConfig.getKeys(false).size() == 1) {
            return false;
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

            boolean idRules = StringUtils.isEmpty(id);
            boolean nameRules = StringUtils.isEmpty(name);
            boolean loreRules = StringUtils.isEmpty(lore);


            //id判断
            if (StringUtils.isNotEmpty(id) && id.equalsIgnoreCase(itemID)) {
                idRules = true;
            }

            //name判断
            if (StringUtils.isNotEmpty(name)) {
                if (name.startsWith("*")) { //模糊匹配
                    if (itemName.toLowerCase().contains(name.replace("*", "").toLowerCase())) {
                        nameRules = true;
                    }
                } else { //普通匹配
                    if (name.equals(itemName)) {
                        nameRules = true;
                    }
                }
            }

            //lore判断
            if (StringUtils.isNotEmpty(lore)) {
                if (itemLore == null) {
                    continue;
                }
                for (String s : itemLore) {
                    if (s.startsWith("*")) { //模糊匹配
                        if (s.toLowerCase().contains(itemName.toLowerCase())) {
                            loreRules = true;
                        }
                    } else { //普通匹配
                        if (s.equals(itemName)) {
                            loreRules = true;
                        }
                    }
                }
            }
            System.out.println("配置节点：" + key);
            System.out.println("|" + itemID + "  || id=" + idRules + "|" + "name=" + nameRules + "|lore=" + loreRules + "|");


            if (idRules && loreRules && nameRules) {
                return false;
            }
        }

        return true;
    }
}
