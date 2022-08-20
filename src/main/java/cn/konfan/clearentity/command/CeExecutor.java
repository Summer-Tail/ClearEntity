package cn.konfan.clearentity.command;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.command.search.Search;
import cn.konfan.clearentity.config.LanguageConfig;
import cn.konfan.clearentity.gui.Bin;
import cn.konfan.clearentity.task.EntityClear;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CeExecutor implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            this.help(sender);
            return true;
        }
        String parm = args[0].toLowerCase();


        if (!sender.isPermissionSet("clearentity.admin") && !("open".equals(parm) || "egg".equals(parm))) {
            return true;
        }

        switch (parm) {

            case "clear":
                this.clear();
                break;
            case "open":
                this.open(sender);
                break;
            case "search":
                this.search(sender, label, args);
                break;
            case "test":
                new EntityClear().run();
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
        return null;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(LanguageConfig.getString("help"));
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
            sender.sendMessage(LanguageConfig.getString("Other.parmError"));
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
        sender.sendMessage(LanguageConfig.getString("Other.reload"));
    }
}
