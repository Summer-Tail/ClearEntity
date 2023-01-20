package cn.konfan.clearentity;

import cn.konfan.clearentity.command.CeExecutor;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.listener.EntityExplodeListener;
import cn.konfan.clearentity.listener.FarmProtectListener;
import cn.konfan.clearentity.listener.GuiListener;
import cn.konfan.clearentity.nms.NMSUtils;
import cn.konfan.clearentity.task.BinClearTask;
import cn.konfan.clearentity.task.EntityNumScanner;
import cn.konfan.clearentity.task.EntityTimer;
import cn.konfan.clearentity.task.VersionScanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public final class ClearEntity extends JavaPlugin {
    private static ClearEntity Instance;

    @Override
    public void onEnable() {
        Instance = this;
        this.init();
    }


    @Override
    public void onDisable() {
        /**
         * Close all player gui
         */
        Bin.closeAllGui();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        LanguageConfig.reload();
    }

    private void init() {
        getLogger().info("Used NMS version: " + ChatColor.GREEN + NMSUtils.getNmsVersion());
        getLogger().info("Loading config and language file...");
        /**
         * Save default config
         */
        saveDefaultConfig();
        /**
         * Save default messages
         */
        if (!(new File(getDataFolder(), "messages.yml").exists())) {
            saveResource("messages.yml", false);
        }
        /**
         * Set commandExecutor
         */
        Objects.requireNonNull(getCommand("clearentity")).setExecutor(new CeExecutor());

        /**
         * Register event listener
         */
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
        Bukkit.getPluginManager().registerEvents(new FarmProtectListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);

        /**
         * Start task
         */
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EntityNumScanner(), 0L, 60 * 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new EntityTimer(), 0L, 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new VersionScanner(), 20, (20 * 60 * 60) * 12);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new BinClearTask(), 0L,  20);
        new Metrics(this, 14080);
    }

    public static ClearEntity getInstance() {
        return Instance;
    }

    public static String convertColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static void SendCountdownUtil(List<Integer> timeList, String message, String replace) {
        /**
         * Sort
         */
        Collections.sort(timeList);

        /**
         * Send beforeMessage
         */
        int maxTime = timeList.get(timeList.size() - 1);
        Bukkit.getScheduler().runTask(ClearEntity.getInstance(), () -> Bukkit.getServer().broadcastMessage(message.replaceAll(replace, "" + maxTime)));
        for (int i = timeList.size() - 2; i >= 0; i--) {
            int time = timeList.get(i);
            Bukkit.getScheduler().runTaskLater(ClearEntity.getInstance(), () -> Bukkit.getServer().broadcastMessage(message.replaceAll(replace, "" + time)), (maxTime - timeList.get(i)) * 20L);
        }
    }


}
