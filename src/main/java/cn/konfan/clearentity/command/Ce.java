package cn.konfan.clearentity.command;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.clear.EntityClear;
import cn.konfan.clearentity.gui.PageGui;
import cn.konfan.clearentity.command.search.SearchEntity;
import cn.konfan.clearentity.task.ClearTask;
import cn.konfan.clearentity.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Ce implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "search", "clear", "reload");
        }
        if (args.length == 2 && "list".equalsIgnoreCase(args[0])) {
            return Arrays.asList("mode", "monster", "animals");
        }
        if (args.length == 2 && "search".equalsIgnoreCase(args[0])) {
            return Arrays.asList("chunk", "entity");
        }
        if (args.length == 3 && "chunk".equalsIgnoreCase(args[1])) {
            return Arrays.asList("10", "100", "1000");
        }
        if (args.length == 3 && "entity".equalsIgnoreCase(args[1])) {
            if (!(sender instanceof Player)) {
                return null;
            }
            List<String> list = new ArrayList<>();
            for (Entity entity : ((Player) sender).getWorld().getEntities()) {
                list.add(Utils.getSaveID(entity));
            }
            return list;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.help(sender);
            return false;
        }

        if (!sender.isPermissionSet("ClearEntity.admin") && !"egg".equals(args[0])) {
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                this.reload(sender);
                break;
            case "list":
                SearchEntity.list(sender, args);
                break;
            case "clear":
                this.clear();
                break;
            case "search":
                this.search(sender, args);
                break;
            case "egg":
                sender.sendMessage("_Godson");
                break;
            case "open":
                if (sender instanceof Player) {
                    PageGui pageGui = new PageGui();
                    pageGui.openGUI((Player) sender);
                }
                break;
            case "c":
                new EntityClear().run();
                break;
            default:
                this.help(sender);

        }
        return false;
    }


    /**
     * 执行一次清理
     */
    private void clear() {
        ClearTask.clearStart();
    }

    /**
     * search命令中转站
     *
     * @param sender 执行人
     * @param args   参数
     */
    private void search(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.getMessage("commandNotConsoleRun"));
            return;
        }

        Player player = (Player) sender;
        try {
            switch (args[1].toLowerCase()) {
                case "chunk":
                    SearchEntity.searchChunk(player, Integer.parseInt(args[2]));
                    break;
                case "entity":
                    SearchEntity.searchEntity(player, args[2], Integer.parseInt(args[3]));
                    break;
                default:
                    sender.sendMessage(Utils.getMessage("paramError"));
            }
        } catch (Exception e) {
            sender.sendMessage(Utils.getMessage("paramError"));
        }


    }






    /**
     * 重新载入配置文件
     *
     * @param sender 操作人
     */
    private void reload(CommandSender sender) {
        ClearEntity.plugin.reloadConfig();
        Utils.reloadMessage();
        sender.sendMessage(Utils.getMessage("reload"));
    }

    private void help(CommandSender sender) {
//        sender.sendMessage("- §a[ClearEntity] §e帮助-------------------#");
//        sender.sendMessage("- §b/ClearEntity clear");
//        sender.sendMessage("- §e立即执行一次清理任务");
//        sender.sendMessage("- §b/ClearEntity list");
//        sender.sendMessage("- §e查询世界中存在的实体列表");
//        sender.sendMessage("- §b/ClearEntity search <chunk/entity>");
//        sender.sendMessage("- §e[chunk]查询当前世界区块中的实体数量");
//        sender.sendMessage("- §e[entity]查询当前世界某实体所在区块及数量");
//        sender.sendMessage("- §b/ClearEntity reload");
//        sender.sendMessage("- §e重新载入配置文件");
//        sender.sendMessage("- §e#------------------------------------#");
//        sender.sendMessage("- §a命令简写模式: /ce");
        sender.sendMessage(Utils.getColorText(Utils.getMessage("help", true)));
    }
}
