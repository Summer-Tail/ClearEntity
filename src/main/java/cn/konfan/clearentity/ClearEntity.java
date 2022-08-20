package cn.konfan.clearentity;

import cn.konfan.clearentity.command.CeExecutor;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.listener.GuiListener;
import cn.konfan.clearentity.task.EntityNumScanner;
import cn.konfan.clearentity.task.EntityTimer;
import cn.konfan.clearentity.task.VersionScanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
        try {
            Metrics metrics = new Metrics(this, 14080);

        } catch (Throwable ignore) {
            //
        }

        /**
         * Save default config
         */
        saveDefaultConfig();
        /**
         * Save default messages
         */
        saveResource("messages.yml", false);

        /**
         * Set commandExecutor
         */
        Objects.requireNonNull(getCommand("clearentity")).setExecutor(new CeExecutor());

        /**
         * Register event listener
         */
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);

        /**
         * Start task
         */
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EntityNumScanner(), 0L, 60 * 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new EntityTimer(), 0L, 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, new VersionScanner(),20);
    }

    public static ClearEntity getInstance() {
        return Instance;
    }

    public static String convertColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
