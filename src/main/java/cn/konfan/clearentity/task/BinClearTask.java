package cn.konfan.clearentity.task;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import org.bukkit.Bukkit;

import java.util.List;

public class BinClearTask implements Runnable {

    int runTime = 0;

    @Override
    public void run() {

        if (!ClearEntity.getInstance().getConfig().getBoolean("EntityManager.Bin.enable")){
            return;
        }


        int cTime = ClearEntity.getInstance().getConfig().getInt("EntityManager.Bin.cTime");
        runTime++;
        if (runTime < cTime) {
            return;
        }

        List<Integer> sendTime = ClearEntity.getInstance().getConfig().getIntegerList("EntityManager.Message.binTime");
        ClearEntity.SendCountdownUtil(sendTime, LanguageConfig.getString("Bin.before"), "%TIME%");
        Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), ()->{
            Bin.clearInv();
            Bukkit.getServer().broadcastMessage(LanguageConfig.getString("Bin.binClear"));
        }, sendTime.get(sendTime.size() - 1) * 20);

        runTime = 0;

    }
}
