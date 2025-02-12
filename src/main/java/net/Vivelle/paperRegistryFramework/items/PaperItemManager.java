package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Method;
import java.util.*;

public class PaperItemManager {
    public static Map<UUID,PaperItem> items = new HashMap<>();
    public static Map<UUID,PaperItem> ticking = new HashMap<>();
    public static Map<NamespacedKey,Class<? extends PaperItem>> ITEM_REGISTRY = new HashMap<>();
    public static NamespacedKey UUID_KEY;
    public static NamespacedKey ID_KEY;

    public static void init(){
        UUID_KEY = new NamespacedKey(PaperRegistryFramework.getInstance(),"UUID");
        ID_KEY = new NamespacedKey(PaperRegistryFramework.getInstance(),"id");
    }

    public static void registerItem(NamespacedKey name, Class<? extends PaperItem> item){
        ITEM_REGISTRY.put(name,item);
    }

    public static void unregisterItem(NamespacedKey name){
        ITEM_REGISTRY.remove(name);
    }

    public static ItemStack instanceItem(NamespacedKey name) {
        try {
            PaperItem item = ITEM_REGISTRY.get(name).getDeclaredConstructor(NamespacedKey.class).newInstance(name);
            createItem(item);
            PaperRegistryFramework.getInstance().getLogger().info(item.getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"none"));
            return item.getItemStack();
        }catch (Exception ignored){
            return ItemStack.empty();
        }
    }
    public static void loadItem(ItemStack item){
        NamespacedKey name = NamespacedKey.fromString(item.getItemMeta().getPersistentDataContainer().get(ID_KEY, PersistentDataType.STRING));
        try {
            PaperItem pitem = ITEM_REGISTRY.get(name).getDeclaredConstructor(NamespacedKey.class).newInstance(name);
            createItem(pitem);
            PaperRegistryFramework.getInstance().getLogger().info(pitem.getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"none"));
        }catch (Exception ignored){
        }
    }

    public static boolean isCustom(ItemStack item){
        return item.getPersistentDataContainer().has(ID_KEY);
    }

    public static void createItem(PaperItem item){
        if(items.containsValue(item)) return;
        items.put(item.getUUID(),item);
        PaperRegistryFramework.getInstance().getLogger().info("adding thingy");
        PaperRegistryFramework.getInstance().getServer().getPluginManager().registerEvents(item,PaperRegistryFramework.getInstance());
        if(hasOnTick(item.getClass())){
            PaperRegistryFramework.getInstance().getLogger().info("adding ticking thingy");
            ticking.put(item.getUUID(),item);
        }
    }

    public static NamespacedKey getItemName(ItemStack item){
        String key = item.getItemMeta().getPersistentDataContainer().getOrDefault(ID_KEY,PersistentDataType.STRING,"");
        if(!key.isEmpty()) return NamespacedKey.fromString(key);
        else return item.getType().getKey();
    }

    public static void removeItem(PaperItem item){
        if(!items.containsValue(item)) return;
        items.remove(item.getUUID());
        ticking.remove(item);
    }
    public static PaperItem getItemById(String uuid){
        return getItemById(UUID.fromString(uuid));
    }
    public static PaperItem getItemById(UUID uuid){
        return items.get(uuid);
    }

    public static PaperItem getItem(ItemStack stack){
        if(stack.getPersistentDataContainer().has(UUID_KEY)) {
            return getItem(stack.getPersistentDataContainer().get(UUID_KEY,PersistentDataType.STRING));
        }
        return null;
    }

    public static PaperItem getItem(String uuid){
        return getItem(UUID.fromString(uuid));
    }

    public static PaperItem getItem(UUID uuid){
        return items.get(uuid);
    }

    public static PaperItem getTickingItem(ItemStack stack){
        if(stack.getPersistentDataContainer().has(UUID_KEY)) {
            return getTickingItem(stack.getPersistentDataContainer().get(UUID_KEY,PersistentDataType.STRING));
        }
        return null;
    }
    public static PaperItem getTickingItem(String uuid){
        return getTickingItem(UUID.fromString(uuid));
    }
    public static PaperItem getTickingItem(UUID uuid){
        return ticking.get(uuid);
    }

    public static boolean hasOnTick(Class<? extends PaperItem> subclass) {
        try {
            // Get the method from the subclass
            for (Method method : subclass.getMethods()) {
                PaperRegistryFramework.getInstance().getLogger().info(String.valueOf(method.getName().equals("onTick")));
            }
            Method method = subclass.getMethod("onTick", Entity.class);
            PaperRegistryFramework.getInstance().getLogger().info("onTick does : "+method);
            return method.getDeclaringClass() != PaperItem.class;

        } catch (NoSuchMethodException e) {
            // If the method does not exist in the superclass, it is not overridden
            PaperRegistryFramework.getInstance().getLogger().warning(e.toString());
            return false;
        }
    }
}
