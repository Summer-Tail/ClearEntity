package xyz.lvsheng.clearentity.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import xyz.lvsheng.clearentity.clear.EntityClear;


/**
 * @author MrLv
 * @date 2022/1/29
 * @apiNote
 */
public class TeListener implements Listener {

    @EventHandler
    public void entityTargetEvent(EntityTargetEvent event) {

        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (target == null) {
            EntityClear.ignore.remove(entity.getEntityId());
            return;
        }
        EntityClear.ignore.add(entity.getEntityId());
    }
}
