package cn.konfan.clearentity;

import cn.konfan.clearentity.command.Ce;
import cn.konfan.clearentity.event.EntitySpawnListener;
import cn.konfan.clearentity.event.ExplodeProtectionListener;
import cn.konfan.clearentity.gui.BinGui;
import cn.konfan.clearentity.gui.GUIListener;
import cn.konfan.clearentity.task.ClearTask;
import cn.konfan.clearentity.task.EntityNumTask;
import cn.konfan.clearentity.utils.Metrics;
import cn.konfan.clearentity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class ClearEntity extends JavaPlugin {
    public static String version;
    public static String method;
    public static ClearEntity plugin;
    public static Integer clearTask;
    @Override
    public void onEnable() {
        // Plugin startup logic
        //统计
        try {
            new Metrics(this, 14080);
        } catch (Throwable ignore) {
            //
        }
        this.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTask(clearTask);

        //关闭所有玩家的GUI
        BinGui.closeAllGui();
    }

    /**
     * 初始化
     */
    public void init() {


        //写出配置文件
        saveDefaultConfig();
        saveResource("messages.yml", false);


        plugin = this;
        version = getVersion();
        method = getMethod();


        //注册命令
        Objects.requireNonNull(Bukkit.getPluginCommand("ClearEntity")).setExecutor(new Ce());

        //清理计时
        clearTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ClearTask(), 20, 20);
        //实体数量监测
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EntityNumTask(), 20, 20);
        //注册事件,用于禁止实体的生成
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), this);
        //注册事件,用于阻止爆炸破坏地形
        Bukkit.getPluginManager().registerEvents(new ExplodeProtectionListener(), this);

        Bukkit.getPluginManager().registerEvents(new GUIListener(),this);

    }

    /**
     * 获取服务器用于反射的版本目录
     *
     * @return 版本名
     */
    private String getVersion() {
        String[] split = Bukkit.getBukkitVersion().split("\\.");
        String version = "v" + split[0] + "_" + split[1].replaceAll("-R.*", "") + "_R";

        for (int i = 1; i < 10; i++) {
            try {
                Class.forName("org.bukkit.craftbukkit." + version + i + ".entity.CraftEntity");
                return version + i;
            } catch (Exception ignore) {
                //忽略异常
            }
        }
        Bukkit.getConsoleSender().sendMessage(Utils.getMessage("versionError"));
        onDisable();
        return null;
    }

    /**
     * 确认反射方法
     *
     * @return 方法名称
     */
    private String getMethod() {
        World world = Bukkit.getWorld("world");
        //临时生成一个实体 用于判断反射方法
        Entity entity = world.spawnEntity(world.getSpawnLocation(), EntityType.BAT);
        try {

            Object craftEntity = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity").cast(entity);
            Method getHandle = craftEntity.getClass().getMethod("getHandle");
            Object nmsEntity = getHandle.invoke(craftEntity);

            //1.12-1.17
            try {
                nmsEntity.getClass().getMethod("getSaveID");
                return "getSaveID";
            } catch (Exception ignore) {
                //
            }

            //1.19 因1.19中也存在bk方法所以需要在前面执行
            try {
                nmsEntity.getClass().getMethod("bo");
                return "bo";
            } catch (Exception ignore) {
                //
            }

            //1.18
            try {
                nmsEntity.getClass().getMethod("bk");
                return "bk";
            } catch (Exception ignore) {
                //
            }


            //return (String) getSaveID.invoke(nmsEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entity.remove();
        }

        Bukkit.getConsoleSender().sendMessage(Utils.getMessage("methodError"));
        onDisable();
        return null;
    }

}
