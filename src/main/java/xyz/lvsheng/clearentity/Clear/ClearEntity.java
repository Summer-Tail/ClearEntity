package xyz.lvsheng.clearentity.Clear;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import xyz.lvsheng.clearentity.Utils.Util;

public class ClearEntity implements Runnable {

    @Override
    public void run() {
        Bukkit.getServer().broadcastMessage(Util.ColorMessage(xyz.lvsheng.clearentity.ClearEntity.configUtil.getClearComplete().replace("%COUNT%", "" + this.clear())));
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
                if (Util.getRules(entity)) {
                    entity.remove();
                    if (entity.isDead()) {
                        remove++;
                    }
                }
            }
        }
        return remove;
    }



}
