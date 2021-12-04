package xyz.lvsheng.clearentity.Clear;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.lvsheng.clearentity.ClearEntity;
import xyz.lvsheng.clearentity.Utils.Util;

import java.util.List;

/**
 * @author MrLv
 * @date 2021/8/19
 * @apiNote
 */
public class ClearTimer extends BukkitRunnable {
    private int time;

    @Override
    public void run() {


        synchronized (this) {

            try {

                time += 60;
                //获取允许被清除的实体列表
                Integer entityNum = Bukkit.getScheduler().callSyncMethod(ClearEntity.plugins, new Util()).get();

                boolean rules =
                        //实体数量大于可清理数量 并且时间大于设定时间
                        (entityNum >= ClearEntity.configUtil.getMinEntity() && time >= ClearEntity.configUtil.getClearTime()) ||
                                //或 实体数量大于最大数量 并且 最大数量不等于-1
                                (entityNum >= ClearEntity.configUtil.getMaxEntity() && ClearEntity.configUtil.getMaxEntity() > 0);


                if (rules) {
                    this.sleep();
                    Bukkit.getScheduler().runTask(ClearEntity.plugins, new xyz.lvsheng.clearentity.Clear.ClearEntity());
                    time = 0;
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void sleep() throws InterruptedException {

        List<Integer> messageTime = ClearEntity.configUtil.getMessageTime();
        for (int i = messageTime.size() - 1; i != -1; i--) {
            Bukkit.getServer().broadcastMessage(Util.ColorMessage(ClearEntity.configUtil.getClearBefore()).replace("%TIME%", "" + messageTime.get(i)));
            if (i != 0) {
                Thread.sleep((messageTime.get(i) - messageTime.get(i - 1)) * 1000L);
            } else {
                Thread.sleep(messageTime.get(i) * 1000L);
            }
        }

    }
}
