package xyz.lvsheng.clearentity;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.lvsheng.clearentity.clear.ClearTimer;
import xyz.lvsheng.clearentity.commands.Ce;
import xyz.lvsheng.clearentity.utils.ConfigUtil;
import xyz.lvsheng.clearentity.utils.Utils;

import java.util.Objects;

public final class ClearEntity extends JavaPlugin {
    public static ClearEntity plugins;
    private BukkitTask task;

    @Override
    public void onEnable() {
        // Plugin startup logic
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
        //计时清理
        task = new ClearTimer().runTaskTimerAsynchronously(this, 20 * 30, 20 * 30);

    }
}
