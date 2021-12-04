package xyz.lvsheng.clearentity.Utils;

import xyz.lvsheng.clearentity.ClearEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ConfigUtil {

    public boolean getMinecraft(){
        return ClearEntity.plugins.getConfig().getBoolean("Minecraft");
    }

    public int getMinEntity() {
        return ClearEntity.plugins.getConfig().getInt("EntityNum.MinEntity");
    }

    public int getMaxEntity() {
        return ClearEntity.plugins.getConfig().getInt("EntityNum.MaxEntity");
    }

    public int getClearTime() {
        return ClearEntity.plugins.getConfig().getInt("ClearTime") <= 60 ? 3600 - ClearEntity.configUtil.getMessageTime().get(ClearEntity.configUtil.getMessageTime().size() - 1) : ClearEntity.plugins.getConfig().getInt("ClearTime") - ClearEntity.configUtil.getMessageTime().get(ClearEntity.configUtil.getMessageTime().size() - 1);
    }

    public String getClearBefore() {
        return ClearEntity.plugins.getConfig().getString("ClearMessages.ClearBefore");
    }

    public String getClearComplete() {
        return ClearEntity.plugins.getConfig().getString("ClearMessages.ClearComplete");
    }

    public List<Integer> getMessageTime() {
        return new ArrayList<>(new TreeSet<>(ClearEntity.plugins.getConfig().getIntegerList("MessageTime")));
    }

    public boolean isDropItems() {
        return ClearEntity.plugins.getConfig().getBoolean("DropItems");
    }

    public boolean isNam() {
        return ClearEntity.plugins.getConfig().getBoolean("Nam");
    }

    public List<String> getEntityWhite() {
        return ClearEntity.plugins.getConfig().getStringList("EntityWhite");
    }

    public List<String> getEntityBlack() {
        return ClearEntity.plugins.getConfig().getStringList("EntityBlack");
    }

    public List<String> getCustomWorld() {
        TreeSet<String> set = new TreeSet<>();
        for (String key : ClearEntity.plugins.getConfig().getKeys(true)) {
            String[] split = key.split("\\.");
            if (split.length == 2 && "CustomWorld".equals(split[0])) {
                set.add(split[1]);
            }
        }
        return new ArrayList<>(set);
    }
}
