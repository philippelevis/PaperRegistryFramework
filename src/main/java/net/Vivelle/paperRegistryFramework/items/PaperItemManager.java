package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PaperItemManager {
    public static Map<UUID,PaperItem> items = new HashMap<>();
    public static List<PaperItem> ticking = new ArrayList<>();
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
            PaperRegistryFramework.getInstance().getLogger().info(item.getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"none"));
            return item.getItemStack();
        }catch (Exception ignored){
            return ItemStack.empty();
        }
    }

    public static boolean createItem(PaperItem item){
        if(items.containsValue(item)) return false;
        items.put(item.getUUID(),item);
        PaperRegistryFramework.getInstance().getServer().getPluginManager().registerEvents(item,PaperRegistryFramework.getInstance());
        if(isMethodOverridden(item.getClass(),"onTick",PaperItem.class)) ticking.add(item);
        return true;
    }

    public static NamespacedKey getItemName(ItemStack item){
        String key = item.getItemMeta().getPersistentDataContainer().getOrDefault(ID_KEY,PersistentDataType.STRING,"");
        if(key != "") return NamespacedKey.fromString(key);
        else return item.getType().getKey();
    }

    public static boolean removeItem(PaperItem item){
        if(!items.containsValue(item)) return false;
        items.remove(item.getUUID());
        ticking.remove(item);
        return true;
    }
    public static PaperItem getItemById(String uuid){
        return getItemById(UUID.fromString(uuid));
    }
    public static PaperItem getItemById(UUID uuid){
        return items.get(uuid);
    }

    public static boolean isMethodOverridden(Class<?> subclass, String methodName, Class<?> superclass) {
        try {
            // Get the method from the subclass
            Method subclassMethod = subclass.getDeclaredMethod(methodName);
            // Get the method from the superclass
            Method superclassMethod = superclass.getDeclaredMethod(methodName);

            // Check if the subclass method is not the same as the superclass method
            return !subclassMethod.equals(superclassMethod);
        } catch (NoSuchMethodException e) {
            // If the method does not exist in the superclass, it is not overridden
            return false;
        }
    }
}
