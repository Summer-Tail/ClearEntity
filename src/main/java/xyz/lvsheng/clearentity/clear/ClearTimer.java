package xyz.lvsheng.clearentity.clear;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.lvsheng.clearentity.ClearEntity;
import xyz.lvsheng.clearentity.utils.ConfigUtil;
import xyz.lvsheng.clearentity.utils.Utils;

import java.util.List;

/**
 * @author MrLv
 * @date 2021/8/19
 * @apiNote
 */
public class ClearTimer extends BukkitRunnable {
    private int time;
    private boolean startClear;

    @Override
    public void run() {

        //丢弃等待任务
        if (startClear) {
            return;
        }

        try {

            time += 30;

            //获取允许被清除的实体列表
            Integer entityNum = Bukkit.getScheduler().callSyncMethod(ClearEntity.plugins, new EntityClear()).get();

            int maxEntity = ConfigUtil.getMaxEntity();
            int minEntity = ConfigUtil.getMinEntity();
            int clearTime = ConfigUtil.getClearTime();


            //实体数量大于设定数量,并且达到了可清理时间 或 实体数量大于最大数量,并且最大数量大于 0
            boolean rules = (entityNum >= minEntity && time >= clearTime) || (entityNum >= maxEntity && maxEntity > 0);

            //不符合规则
            if (!rules) {
                return;
            }


            try {
                startClear = true;
                this.sleep();
                Bukkit.getScheduler().runTask(ClearEntity.plugins, new EntityClear());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                startClear = false;
                time = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleep() throws InterruptedException {

        List<Integer> time = ConfigUtil.getMessageTime();

        for (int i = time.size() - 1; i != -1; i--) {
            Bukkit.getServer().broadcastMessage(Utils.colorMessage(ConfigUtil.getClearBefore()).replace("%TIME%", "" + time.get(i)));


            if (i != 0) {
                Thread.sleep((time.get(i) - time.get(i - 1)) * 1000L);
                continue;
            }

            Thread.sleep(time.get(i) * 1000L);
        }

    }
}
