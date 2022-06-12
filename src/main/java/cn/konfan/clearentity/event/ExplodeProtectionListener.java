package cn.konfan.clearentity.event;

import cn.konfan.clearentity.utils.Utils;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeProtectionListener implements Listener {
    /**
     * 禁止实体爆炸 破坏地形
     * @param event 爆炸事件
     */
    @EventHandler
    public void Explosion(EntityExplodeEvent event){
        if (event.getEntity() instanceof TNTPrimed && Utils.getConfig().getBoolean("Explode.Tnt")){
            event.setCancelled(true);
        }
        if (event.getEntity() instanceof Creeper && Utils.getConfig().getBoolean("Explode.Creeper")){
            event.setCancelled(true);
        }
    }
}
