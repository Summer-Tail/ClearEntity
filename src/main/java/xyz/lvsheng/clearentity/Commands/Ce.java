package xyz.lvsheng.clearentity.Commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import xyz.lvsheng.clearentity.ClearEntity;
import xyz.lvsheng.clearentity.Utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ce implements TabExecutor {
    private CommandSender sender;

    @Override
    public List<String> onTabComplete(CommandSender senders, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("clear");
            list.add("list");
            list.add("reload");
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender senders, Command command, String label, String[] args) {
        sender = senders;
        this.command(args.length == 0 ? "help" : args[0]);
        return true;
    }

    private void command(String str) {
        switch (str.toLowerCase()) {
            case "clear":
                this.clear();
                break;
            case "list":
                this.list();
                break;
            case "reload":
                this.reload();
                break;
            case "test":
                this.test();
                break;
            default:
                this.help();
                break;
        }
    }


    private void clear() {
        Bukkit.getScheduler().runTask(ClearEntity.plugins, new xyz.lvsheng.clearentity.Clear.ClearEntity());
    }


    private void list() {

        HashMap<String, Integer> map = new HashMap<>();
        int entityNum = 0;
        for (World world : Bukkit.getWorlds())
            for (Entity entity : world.getEntities()) {
                if (!"Player".equalsIgnoreCase(entity.getType().name())) {
                    String id = Util.getID(entity);
                    if (id != null) {
                        if (map.containsKey(id)) {
                            map.put(id, map.get(id) + 1);
                        } else {
                            map.put(id, 1);
                        }
                    } else {
                        if (map.containsKey("未知实体")) {
                            map.put("未知实体", map.get("未知实体") + 1);
                        } else {
                            map.put("未知实体", 1);
                        }
                    }
                    entityNum++;
                }
            }

        sender.sendMessage("- §b共发现了 §e" + entityNum + " §b个实体 种类: §e" + map.size());
        try {
            //向下兼容低版本 低版本中无此类
            Class.forName("net.md_5.bungee.api.chat.hover.content.Content");

            for (String s : map.keySet()) {
                net.md_5.bungee.api.chat.BaseComponent url = new TextComponent("- §b" + s + " §e" + map.get(s));
                url.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击复制到剪切版")));
                url.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "  - '" + s + "'"));
                sender.spigot().sendMessage(url);
            }


        } catch (Exception e) {


            for (String s : map.keySet()) {
                BaseComponent url = new TextComponent("- §b" + s + " §e" + map.get(s));
                url.setHoverEvent(new HoverEvent
                        (HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§a点击将配置节点到输入框\nCtrl+A全选\nCtrl+X剪切(复制)")}));
                url.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "  - '" + s + "'"));
                sender.spigot().sendMessage(url);
            }


        }
    }

    private void reload() {
        ClearEntity.plugins.reloadConfig();
        sender.sendMessage("§a[ClearEntity] §e配置文件重载成功!");
    }

    private void help() {
        sender.sendMessage("- §a[ClearEntity] §e帮助-------------------#");
        sender.sendMessage("- §b/ClearEntity clear:");
        sender.sendMessage("- §e立即清理一次实体(不会有倒计时)");
        sender.sendMessage("- §b/ClearEntity list ");
        sender.sendMessage("- §e查询世界中存在的实体列表");
        sender.sendMessage("- §b/ClearEntity reload");
        sender.sendMessage("- §e重新载入配置文件");
        sender.sendMessage("- §e#------------------------------------#");
        sender.sendMessage("- §a命令简写模式: /ce");
    }

    private void test() {

    }

}
