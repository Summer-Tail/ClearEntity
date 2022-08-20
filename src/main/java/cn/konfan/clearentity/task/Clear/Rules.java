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

import java.util.List;

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
        if (!config.getBoolean("EntityManager.Rename") && !StringUtils.isBlank(entity.getCustomName())) {
            return false;
        }


        /**
         * Rules Selection
         */
        ConfigurationSection rules = config.getConfigurationSection("EntityManager.Rules.custom." + entity.getWorld().getName());
        rules = rules != null ? rules : config.getConfigurationSection("EntityManager.Rules");


        /**
         * Whitelist
         */
        List<String> white = rules.getStringList("whitelist");
        if (white.contains(saveID)) {
            return false;
        }
        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            if ((white.contains("animals") && entity instanceof Animals) || white.contains("monster") && entity instanceof Monster) {
                System.out.println(saveID);
                return false;
            }
        }

        /**
         * Blacklist
         */
        List<String> black = rules.getStringList("blacklist");
        if (black.contains(saveID)) {
            return true;
        }
        if (!config.getBoolean("EntityManager.Rules.mode") || saveID.startsWith("minecraft:")) {
            if ((black.contains("monster") && entity instanceof Monster) || (black.contains("animals") && entity instanceof Animals)) {
                return true;
            }
        }


        /**
         *  unknown
         */
        return false;
    }

    public static boolean getItemRules(Entity item) {
        config = ClearEntity.getInstance().getConfig();

        ItemStack itemStack = ((Item) item).getItemStack();
        String itemName = itemStack.getItemMeta().getDisplayName();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        String itemID = NMSUtils.getItemID(itemStack);


        ConfigurationSection section = config.getConfigurationSection("EntityManager.Rules.custom." + item.getWorld().getName());
        ConfigurationSection itemConfig = config.getConfigurationSection("EntityManager.Rules.item");
        itemConfig = section == null ? itemConfig : section;

        for (String key : itemConfig.getKeys(false)) {
            if ("enable".equals(key)) continue;

            String id = itemConfig.getString(key + ".id");
            String name = itemConfig.getString(key + ".name");
            String lore = itemConfig.getString(key + ".lore");

            if (StringUtils.isNotEmpty(id) && id.equals(itemID)) {
                return false;
            }

            if (StringUtils.isNotEmpty(name)) {
                if (name.startsWith("*")) {
                    if (name.contains(itemName)) return false;
                } else {
                    if (name.equals(itemName)) return false;
                }
            }

            if (StringUtils.isNotEmpty(lore)) {
                for (String s : itemLore) {
                    if (lore.startsWith("*")) {
                        if (lore.contains(s)) return false;
                    } else {
                        if (lore.equals(s)) return false;
                    }
                }
            }


        }


        return true;
    }
}
