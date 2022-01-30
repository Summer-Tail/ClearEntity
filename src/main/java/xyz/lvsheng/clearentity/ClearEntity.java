package xyz.lvsheng.clearentity;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.lvsheng.clearentity.clear.ClearTimer;
import xyz.lvsheng.clearentity.commands.Ce;
import xyz.lvsheng.clearentity.event.TeListener;
import xyz.lvsheng.clearentity.utils.Metrics;
import xyz.lvsheng.clearentity.utils.Utils;

import java.util.Objects;

public final class ClearEntity extends JavaPlugin {
    public static ClearEntity plugins;
    private BukkitTask task;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            new Metrics(this, 14080);
        }catch (Throwable ignore){
            //
        }

        this.init();
        Bukkit.getConsoleSender().sendMessage(Utils.colorMessage("&a[ClearEntity] &e插件加载成功!"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        task.cancel();
        Bukkit.getConsoleSender().sendMessage(Utils.colorMessage("&a[ClearEntity] &c插件卸载成功!"));
    }

    private void init() {
        plugins = this;
        saveDefaultConfig();
        //注册命令
        Objects.requireNonNull(getCommand("ClearEntity")).setExecutor(new Ce());
        //注册事件
        Bukkit.getPluginManager().registerEvents(new TeListener(),this);
        //计时清理
        task = new ClearTimer().runTaskTimerAsynchronously(this, 20 * 30, 20 * 30);
        if (this.getServer().getVersion().toLowerCase().contains("mohist")) throw new RuntimeException("不支持Mohist");
    }
}
