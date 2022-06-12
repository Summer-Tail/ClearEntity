package cn.konfan.clearentity.clear;

import cn.konfan.clearentity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.List;
import java.util.Set;


public class EntityClear implements Runnable {


    /**
     * 确认实体是否应该被清理
     *
     * @param entity 实体
     * @return 是否清理
     */
    private boolean getRules(Entity entity) {

        //玩家不可被清理
        if (entity instanceof Player) {
            return false;
        }

        //命名生物处理
        if (!Utils.getConfig().getBoolean("Rules.Nam")) {
            if (entity.getCustomName() == null) {
                return false;
            }
        }

        //实体id
        String saveID = Utils.getSaveID(entity);

        List<String> white = null;
        List<String> black = null;
        //规则选择
        if (Utils.getConfig().contains("Rules.World." + entity.getWorld().getName())) {
            white = Utils.getConfig().getStringList("Rules.World." + entity.getWorld().getName() + ".White");
            black = Utils.getConfig().getStringList("Rules.World." + entity.getWorld().getName() + ".Black");
        } else {
            white = Utils.getConfig().getStringList("Rules.White");
            black = Utils.getConfig().getStringList("Rules.Black");
        }

        //掉落物
        if (entity instanceof Item && black.contains("minecraft:item")) {

            try {
                Set<String> keys = Utils.getConfig().getConfigurationSection("Rules.ItemWhite").getKeys(false);
                for (String key : keys) {
                    String id = Utils.getConfig().getString("Rules.ItemWhite." + key + ".id");
                    String name = Utils.getConfig().getString("Rules.ItemWhite." + key + ".name");
                    String lore = Utils.getConfig().getString("Rules.ItemWhite." + key + ".lore");

                    //id处理
                    if (id != null && !id.equalsIgnoreCase(((Item) entity).getItemStack().getType().name())) {
                        return true;
                    }

                    //name处理
                    if (name != null && !Utils.like(name, ((Item) entity).getItemStack().getItemMeta().getDisplayName())) {
                        return true;
                    }


                    if (lore != null) {
                        return false;
                    }

                    List<String> itemLore = ((Item) entity).getItemStack().getItemMeta().getLore();
                    if (itemLore == null) {
                        return true;
                    }


                    for (String s : itemLore) {
                        if (!Utils.like(lore, s)) {
                            return true;
                        }
                    }

                    return false;
                }
            } catch (NullPointerException ignore) {
                //
            }

            return true;
        }

        //白名单
        if (white.contains(saveID)) {
            return false;
        }

        //黑名单
        if (black.contains(saveID)) {
            //判断船或矿车上是否有玩家
            if (entity instanceof Boat || entity instanceof Minecart) {
                return entity.getPassengers().size() == 0;
            }
            return true;
        }


        //非原版实体不参与 [动物] 与 [怪物] 的分类判断
        if (!saveID.startsWith("minecraft:") && Utils.getConfig().getBoolean("Rules.Mode")) {
            return false;
        }


        //============================================================================
        //白名单 动物
        if (white.contains("animals") && entity instanceof Animals) {
            return false;
        }
        //白名单 怪物
        if (white.contains("monster") && entity instanceof Monster) {
            return false;
        }

        //黑名单
        if (black.contains("monster") && entity instanceof Monster) {
            return true;
        }


        if (black.contains("animals")) {
            return entity instanceof Animals;
        }


        //未知
        return false;
    }

    @Override
    public void run() {
        int num = 0;
        for (World world : Bukkit.getWorlds()) {

            for (Entity entity : world.getEntities()) {

                if (getRules(entity)) {
                    entity.remove();
                    num++;
                }

            }

        }

        Bukkit.getServer().broadcastMessage(Utils.getColorText(Utils.getConfig().getString("Message.Clear").replaceAll("%COUNT%", "" + num)));

    }


}
