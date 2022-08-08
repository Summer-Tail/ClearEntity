package cn.konfan.clearentity.event;

import cn.konfan.clearentity.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {

    /**
     * 禁止某些实体的生成
     *
     * @param event 实体生成事件
     */
    @EventHandler
    public void banEntity(EntitySpawnEvent event) {
        if (Utils.getConfig().getStringList("EntityBlack").size() == 0) {
            return;
        }
        if (Utils.getConfig().getStringList("EntityBlack").contains(Utils.getSaveID(event.getEntity()))) {
            event.setCancelled(true);
        }
    }


}
