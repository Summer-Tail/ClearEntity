package cn.konfan.clearentity.command.search;

import cn.konfan.clearentity.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SearchEntity {
    /**
     * 显示玩家所在世界某实体所在区块及数量
     *
     * @param player 执行人
     * @param id     实体id
     * @param min    忽略数量
     */
    public static void searchEntity(Player player, String id, Integer min) {
        Chunk[] loadedChunks = player.getWorld().getLoadedChunks();
        Map<Chunk, Integer> map = new HashMap<>();
        for (Chunk loadedChunk : loadedChunks) {
            for (Entity entity : loadedChunk.getEntities()) {
                String saveID = Utils.getSaveID(entity);
                if (!saveID.equalsIgnoreCase(id)) {
                    continue;
                }
                Integer entityNum = map.get(loadedChunk);
                map.put(loadedChunk, entityNum == null ? 1 : entityNum + 1);
            }
        }

        if (map.size() == 0) {
            player.sendMessage(Utils.getMessage("noSearchChunk"));
            return;
        }

        int showNum = 0;
        for (Chunk chunk : map.keySet()) {

            if (map.get(chunk) < min) {
                continue;
            }
            showNum++;
            HoverEvent hoverEvent = new HoverEvent
                    (HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Utils.getColorText(Utils.getMessage("clickTpChunk", true)))});
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + chunk.getX() * 16 + " " + player.getLocation().getY() + " " + chunk.getZ() * 16);
            player.spigot().sendMessage(Utils.jsonText(Utils.getColorText(Utils.getMessage("showFormat", true).replaceAll("%CSYS%", "[X:" + chunk.getX() + ",Z:" + chunk.getZ() + "]").replaceAll("%COUNT%", map.get(chunk) + "")), hoverEvent, clickEvent));
        }

        if (showNum == 0) {
            player.sendMessage(Utils.getMessage("noSearchChunk"));
            return;
        }
        player.sendMessage(Utils.getMessage("searchChunk").replaceAll("%COUNT%", showNum + ""));

    }


    /**
     * 显示玩家所在世界所有区块内实体数量
     *
     * @param player 玩家
     * @param min    忽略实体小于此数量的区块
     */
    public static void searchChunk(Player player, Integer min) {
        int num = 0;
        Chunk[] loadedChunks = player.getWorld().getLoadedChunks();
        for (Chunk loadedChunk : loadedChunks) {
            if (loadedChunk.getEntities().length < min) {
                continue;
            }
            num++;

            HoverEvent hoverEvent = new HoverEvent
                    (HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Utils.getColorText(Utils.getMessage("clickTpChunk", true)))});
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + loadedChunk.getX() * 16 + " " + player.getLocation().getY() + " " + loadedChunk.getZ() * 16);
            player.spigot().sendMessage(Utils.jsonText(Utils.getColorText(Utils.getMessage("showFormat", true).replaceAll("%CSYS%", "[X:" + loadedChunk.getX() + ",Z:" + loadedChunk.getZ() + "]").replaceAll("%COUNT%", loadedChunk.getEntities().length + "")), hoverEvent, clickEvent));

        }

        if (num == 0) {
            player.sendMessage(Utils.getMessage("noSearchChunk"));
            return;
        }
        player.sendMessage(Utils.getMessage("searchChunk").replaceAll("%COUNT%", num + ""));

    }


    /**
     * 列出服务器中所有的实体以及数量
     *
     * @param args 仅显示那种实体
     */
    public static void list(CommandSender sender, String[] args) {
        Map<String, Integer> map = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                String saveID = Utils.getSaveID(entity);
                saveID = "".equals(saveID) ? "unknown" : saveID;


                if (args.length == 2) {
                    //仅存入非原版实体
                    if ("mode".equalsIgnoreCase(args[1]) && saveID.startsWith("minecraft:")) {
                        continue;
                    }
                    //仅存入怪物分类实体
                    if ("monster".equalsIgnoreCase(args[1]) && !(entity instanceof Monster)) {
                        continue;
                    }
                    //仅存入动物实体
                    if ("animals".equalsIgnoreCase(args[1]) && !(entity instanceof Animals)) {
                        continue;
                    }
                }

                Integer num = map.get(Utils.getSaveID(entity));
                map.put(saveID, num == null ? 1 : num + 1);
            }
        }

        if (map.keySet().size() == 0) {
            sender.sendMessage(Utils.getMessage("listNoScanner"));
            return;
        }


        sender.sendMessage(Utils.getMessage("listScannerNum").replaceAll("%COUNT%", map.size() + ""));
        try {
            //向下兼容低版本 低版本中无此类
            Class.forName("net.md_5.bungee.api.chat.hover.content.Content");

            for (String s : map.keySet()) {
                net.md_5.bungee.api.chat.BaseComponent url = new TextComponent("- §b" + s + " §e" + map.get(s));
                url.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.getColorText(Utils.getMessage("newJsonTextClick", true)))));
                url.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "  - '" + s + "'"));
                sender.spigot().sendMessage(url);
            }


        } catch (Exception e) {


            for (String s : map.keySet()) {
                BaseComponent url = new TextComponent("- §b" + s + " §e" + map.get(s));
                url.setHoverEvent(new HoverEvent
                        (HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Utils.getColorText(Utils.getMessage("oldJsonTextClick", true)))}));
                url.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "  - '" + s + "'"));
                sender.spigot().sendMessage(url);
            }


        }
    }
}
