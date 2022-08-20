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

        if (!itemConfig.getBoolean("enable")) {
            return false;
        }

        boolean idRules = true;
        boolean nameRules = true;
        boolean loreRules = true;

        for (String key : itemConfig.getKeys(false)) {
            if ("enable".equals(key)) continue;
            String id = itemConfig.getString(key + ".id");
            String name = itemConfig.getString(key + ".name");
            String lore = itemConfig.getString(key + ".lore");

            /**
             * Null if
             */
            if (StringUtils.isNotEmpty(lore) && itemLore == null) {
                return true;
            }

            if (StringUtils.isNotEmpty(name) && StringUtils.isEmpty(itemName)) {
                return true;
            }


            /**
             * id判断
             */
            if (StringUtils.isNotEmpty(id)) {
                idRules = id.equals(itemID);
            }

            /**
             * 名字判断
             */
            if (StringUtils.isNotEmpty(name)) {
                nameRules = itemName.startsWith("*") ? name.contains(itemName) : name.equals(itemName);
            }

            /**
             * 描述判断
             */
            if (StringUtils.isNotEmpty(lore)) {
                for (String s : itemLore) {

                    loreRules = s.startsWith("*") ? lore.contains(itemName) : lore.equals(itemName);

                    if (idRules && nameRules && loreRules) {
                        return false;
                    }
                }
            }
        }
        return !idRules || !nameRules || !loreRules;
    }
}
