/**
 * NeverLagReborn - Kotori0629, MrLv0816
 * Copyright (C) 2022-2022.
 */
package cn.konfan.clearentity.nms;


import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NMSUtils {

    private static Object nmsEntity;
    private static String methodName;
    private static Entity tempEntity;

    static {
        try {
            World world = Bukkit.getWorld(Bukkit.getWorld("DIM1") != null ? "DIM1" : "world_the_end");
            world = world == null ? Bukkit.getWorlds().get(0) : world;
            tempEntity = world.spawnEntity(world.getSpawnLocation(), EntityType.ENDERMAN);
            nmsEntity = Class.forName("org.bukkit.craftbukkit." + getNmsVersion() + ".entity.CraftEntity").cast(tempEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getSaveID method from nmsClass : Entity
     *
     * @return SaveID
     */
    public static String getSaveID(Entity entity) {
        try {
            Object craftEntity = Class.forName("org.bukkit.craftbukkit." + getNmsVersion() + ".entity.CraftEntity").cast(entity);
            Method getHandle = craftEntity.getClass().getMethod("getHandle");
            Object nmsEntity = getHandle.invoke(craftEntity);
            Object saveId = nmsEntity.getClass().getMethod(getSaveIDMethodName()).invoke(nmsEntity);
            return saveId == null ? "" : saveId.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getItemID(ItemStack stack) {
        String[] methods = {"save", "b"};
        String[] get = {"get", "l"};
        String[] paths =
                {
                        "net.minecraft.server." + getNmsVersion() + ".NBTTagCompound",
                        "net.minecraft.nbt.NBTTagCompound"
                };
        try {
            //ItemStack nmsItemStack =CraftItemStack.asNMSCopy(stack);
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + getNmsVersion() + ".inventory.CraftItemStack");
            Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItemStack = asNMSCopy.invoke(asNMSCopy, stack);


            //nmsItemStack.save(new NBTTagCompound());
            Class<?> NBTTagCompoundClass = null;


            for (String path : paths) {
                try {
                    NBTTagCompoundClass = Class.forName(path);
                    break;
                } catch (ClassNotFoundException ignore) {
                    //
                }
            }

            Method itemSave = null;
            for (String method : methods) {
                try {
                    itemSave = nmsItemStack.getClass().getMethod(method, NBTTagCompoundClass);
                    break;
                } catch (NoSuchMethodException ignore) {
                    //
                }
            }
            Object nbt = itemSave.invoke(nmsItemStack, NBTTagCompoundClass.newInstance());


            Method nbtGet = null;
            for (String s : get) {
                try {
                    nbtGet = nbt.getClass().getMethod(s, String.class);
                } catch (NoSuchMethodException ignore) {
                    //
                }
            }

            Object id = nbtGet.invoke(nbt, "id");

            return id == null ? "" : id.toString().replaceAll("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * getSaveID method from nmsClass : Entity
     *
     * @return getSaveID method
     */
    private static String getSaveIDMethodName() {
        if (StringUtils.isNotEmpty(methodName)) {
            return methodName;
        }
        try {

            Method getHandle = nmsEntity.getClass().getMethod("getHandle");
            nmsEntity = getHandle.invoke(nmsEntity);

            for (Method method : nmsEntity.getClass().getMethods()) {
                if (!"class java.lang.String".equals(method.getGenericReturnType().toString())) {
                    continue;
                }
                if (method.getName().equalsIgnoreCase("getSaveID")) {
                    methodName = method.getName();
                } else if (method.getName().equalsIgnoreCase("bo")) {
                    methodName = method.getName();
                } else if (method.getName().equalsIgnoreCase("bk")) {
                    methodName = method.getName();
                } else if (method.getName().equalsIgnoreCase("bn")) {
                    methodName = method.getName();
                }
            }
            nmsEntity.getClass().getMethod(methodName).invoke(nmsEntity);
            return methodName;
        } catch (Exception e) {
            throw new RuntimeException("Cannot setup getSaveIDMethodName: " + e.getMessage());
        } finally {
            try {
                tempEntity.remove();
            } catch (NullPointerException ignore) {
                //
            }
        }
    }

    /**
     * Get nms version
     *
     * @return Server native version
     */
    private static String getNmsVersion() {
        try {
            String nmsVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
            Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".CraftServer");
            return nmsVersion;
        } catch (Exception e) {
            throw new RuntimeException("Cannot setup nmsVersion " + e.getMessage() + e);
        }
    }
}
