package xyz.lvsheng.clearentity;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.lvsheng.clearentity.Clear.ClearTimer;
import xyz.lvsheng.clearentity.Commands.Ce;
import xyz.lvsheng.clearentity.Utils.ConfigUtil;
import xyz.lvsheng.clearentity.Utils.Util;

import java.util.Objects;

public final class ClearEntity extends JavaPlugin {
    public static ClearEntity plugins;
    public static ConfigUtil configUtil;
    private BukkitTask task;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.init();
        Bukkit.getConsoleSender().sendMessage(Util.ColorMessage("&a[ClearEntity] &e插件加载成功!"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        task.cancel();
        Bukkit.getConsoleSender().sendMessage(Util.ColorMessage("&a[ClearEntity] &c插件卸载成功!"));
    }

    private void init() {
        plugins = this;
        saveDefaultConfig();
        //读配置文件
        configUtil = new ConfigUtil();
        //注册命令
        Objects.requireNonNull(getCommand("ClearEntity")).setExecutor(new Ce());
        //计时清理
        task = new ClearTimer().runTaskTimerAsynchronously(this, 20 * 60, 20 * 60);

    }
}
