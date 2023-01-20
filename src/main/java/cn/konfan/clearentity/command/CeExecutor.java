package cn.konfan.clearentity.command;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.command.search.Search;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.task.EntityClear;
import cn.konfan.clearentity.task.ItemClear;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class CeExecutor implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.help(sender);
            return true;
        }
        String param = args[0].toLowerCase();


        if (!sender.isPermissionSet("clearentity.admin") && !("open".equals(param) || "egg".equals(param))) {
            return true;
        }

        switch (param) {
            case "clear":
                if (args.length >= 2 && "true".equals(args[1].toLowerCase())) {
                    this.rightClear();
                } else {
                    this.clear();
                }
                break;
            case "open":
                if (sender.isPermissionSet("clearentity.open")) {
                    this.open(sender);
                }
                break;
            case "search":
                this.search(sender, label, args);
                break;
            case "egg":
                sender.sendMessage("_Godson: Hi~");
                break;
            case "reload":
                this.reload(sender);
                break;
            default:
                help(sender);
                break;

        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.isPermissionSet("clearentity.admin")) {
            switch (args.length) {
                case 1:
                    return Arrays.asList("clear", "open", "search", "reload");
                case 2:
                    return Arrays.asList("true", "list", "chunk");
                case 3:
                    Map<String, Integer> map = Search.searchEntity(null);
                    return new ArrayList<>(map.keySet());
                case 4:
                    return Arrays.asList("20", "40", "80", "100");
                default:
                    return Collections.singletonList("未知参数....");
            }
        } else if (sender.hasPermission("clearentity.open")) {
            if (args.length == 1) {
                return Collections.singletonList("open");
            }
        }
        return null;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(LanguageConfig.getString("help"));
    }


    private void rightClear() {
        new EntityClear().run();
    }


    private void clear() {
        EntityClear.clearStart();
    }

    private void search(CommandSender sender, String label, String[] args) {
        try {
            int num = 0;
            String entityID = null;
            if (args.length == 3) {
                num = Integer.parseInt(args[2]);
            }
            if (args.length == 4) {
                entityID = args[2];
                num = Integer.parseInt(args[3]);
            }
            switch (args[1].toLowerCase()) {
                case "list":
                    Search.sendMessage(sender, Search.searchEntity(entityID), num);
                    break;
                case "chunk":
                    Search.sendMessage(sender, Search.searchChunk(entityID), num);
                    break;
                case "entity":
                    break;
            }
        } catch (Exception e) {
            sender.sendMessage(LanguageConfig.getString("Other.paramError"));
        }

    }

    private void open(CommandSender sender) {
        if (sender instanceof Player) {
            Bin.open((Player) sender, 0);
        } else {
            sender.sendMessage(LanguageConfig.getString("Other.notConsole"));
        }
    }

    private void reload(CommandSender sender) {
        ClearEntity.getInstance().reloadConfig();
        Bin.clearInv();
        sender.sendMessage(LanguageConfig.getString("Other.reload"));
    }
}
