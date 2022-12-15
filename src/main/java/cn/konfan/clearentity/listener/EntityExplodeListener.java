package cn.konfan.clearentity.listener;


import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.nms.NMSUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class EntityExplodeListener implements Listener {


    @EventHandler
    public void EntityExplode(EntityExplodeEvent event) {
        String saveID = NMSUtils.getSaveID(event.getEntity());
        List<String> entityList = ClearEntity.getInstance().getConfig().getStringList("EntityManager.EntityExplode");
        entityList.forEach(entity -> {
            if (entity.equalsIgnoreCase(saveID)){
                event.setCancelled(true);
            }
        });
    }


}
