package net.Vivelle.paperRegistryFramework;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.Vivelle.paperRegistryFramework.handlers.EntityDeathListener;
import net.Vivelle.paperRegistryFramework.handlers.PlayerJoinListener;
import net.Vivelle.paperRegistryFramework.handlers.PlayerLeaveListener;
import net.Vivelle.paperRegistryFramework.handlers.PrepareCraftListener;
import net.Vivelle.paperRegistryFramework.items.PaperItem;
import net.Vivelle.paperRegistryFramework.items.PaperItemManager;
import net.Vivelle.paperRegistryFramework.loottables.LootTableRegistry;
import net.Vivelle.paperRegistryFramework.util.MyEntityEffect;
import net.Vivelle.paperRegistryFramework.util.MyTickable;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public final class PaperRegistryFramework extends JavaPlugin {
    private static final Logger log = LoggerFactory.getLogger(PaperRegistryFramework.class);
    private static NamespacedKey EffectListKey = null;
    private static NamespacedKey keyEffect = null;
    public static NamespacedKey KarmaKey;
    public static ArrayList<MyTickable> Tickers = new ArrayList<>();
    public static HashMap<String, Class<? extends MyEntityEffect>> Effects = new HashMap<>();
    public static HashMap<Entity, ArrayList<MyEntityEffect>> AppliedEffects = new HashMap<>();
    public static LootTableRegistry lootTableRegistry;
    private static PaperRegistryFramework instance;
    private static final ReentrantLock lock = new ReentrantLock();
    @Override
    public void onEnable() {
        instance = this;
        EffectListKey = new NamespacedKey(this, "CustomEffects");
        keyEffect = new NamespacedKey(this, "effect");
        // Plugin startup logic
        KarmaKey = new NamespacedKey(this, "karma");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PrepareCraftListener(),this);
        BukkitScheduler ticker = getServer().getScheduler();

        lootTableRegistry = new LootTableRegistry();
        ticker.runTaskTimer(this, ()->{
            lock.lock();
            try {
                ArrayList<MyTickable> tickersCopy;
                synchronized (Tickers) {
                    tickersCopy = new ArrayList<>(Tickers);
                }
                for (MyTickable tickable : tickersCopy) {
                    if (tickable != null) tickable.tick();
                }
            }finally {
                lock.unlock();
            }
        },0,1);

        createLootTableDirectory();
        loadLootTables();
        PaperItemManager.init();
    }

    public static void killEffects(Entity entity){
        lock.lock();
        try {
            ArrayList<MyEntityEffect> lst = new ArrayList<>(Optional.ofNullable(AppliedEffects.get(entity)).orElseGet(ArrayList::new));
            for (MyEntityEffect effect : lst) {
                effect.remove();
            }
            AppliedEffects.remove(entity);
        }finally {
            lock.unlock();
        }
    }

    public static boolean isEffectApplied(Entity ent, String effectName){
        try{
        List<PersistentDataContainer> immutableList = ent.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        for (PersistentDataContainer container : immutableList) {
            String str = container.get(keyEffect, PersistentDataType.STRING);
            if (str != null && str.equals(effectName)) return true;
        }}catch (Exception ignored){
            return false;
        }
        return false;
    }

    public static MyEntityEffect getAppliedEffect(Entity ent, String effectName){
        for (MyEntityEffect eff : AppliedEffects.get(ent)) {
            if (eff.getEffectName(null).equals(effectName)) return eff;
        }
        return null;
    }

    public static HashMap<Entity, ArrayList<MyEntityEffect>> getAppliedEffects() {
        return AppliedEffects;
    }

    public static int getKarma(Player player){
        return player.getPersistentDataContainer().getOrDefault(PaperRegistryFramework.KarmaKey, PersistentDataType.INTEGER, 0);
    }
    public static void setKarma(Player player, int value){
        player.getPersistentDataContainer().set(PaperRegistryFramework.KarmaKey, PersistentDataType.INTEGER, value);
    }

    public static void ApplyEffect(Class<? extends MyEntityEffect> effect, Player plr) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            effect.getDeclaredConstructor(int.class, Player.class).newInstance(20*60, plr);
    }


    public static void RegisterEffect(String name, Class<? extends MyEntityEffect> effect){
        Effects.put(name,effect);
    }

    public static Class<? extends MyEntityEffect> getEffect(String name){
        Class<? extends MyEntityEffect> eff = Effects.get(name);
        if(eff != null){
            return eff;
        }else{
            throw new IllegalArgumentException("No valid effect found for key: " + name);
        }
    }

    public static LootTableRegistry getLootRegistry(){
        return lootTableRegistry;
    }

    public static void RemoveTicker(MyTickable tickable){
        lock.lock();
        try {
            //getInstance().getLogger().info(String.valueOf(Tickers.remove(tickable)));
            if(tickable instanceof MyEntityEffect effect){
                List<MyEntityEffect> lst = getAppliedEffects().get(effect.getEntity());
                if(lst != null) lst.remove(effect);
            }
        }finally {
            lock.unlock();
        }
    }

    public static void AddTicker(MyTickable tickable){
        Tickers.add(tickable);
    }
    public static boolean HasTickable(MyTickable tickable){
        return Tickers.contains(tickable);
    }

    public static PaperRegistryFramework getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        List<MyTickable> lst = new ArrayList<>(Tickers);
        lock.lock();
        lst.forEach(tickable->{
            if(tickable != null) {
                RemoveTicker(tickable);
                tickable.remove();
            }
        });
        lock.unlock();
    }

    private void createLootTableDirectory() {
        File lootTableDir = new File(getDataFolder(), "loot_tables");
        if (!lootTableDir.exists()) {
            lootTableDir.mkdirs(); // Create the directory if it doesn't exist
        }
        File singlelootTableDir = new File(lootTableDir, "singles");
        getLogger().info(singlelootTableDir.getAbsolutePath());
        if (!singlelootTableDir.exists()) {
            singlelootTableDir.mkdirs(); // Create the directory if it doesn't exist
        }
    }

    private void loadLootTables() {
        File lootTableDir = new File(getDataFolder(), "loot_tables");
        File[] files = lootTableDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()));
                    String lootTableName = file.getName().replace(".json", ""); // Get the file name without extension
                    lootTableRegistry.loadLootTableFromJson(lootTableName, json);
                    getLogger().info("Loaded loot table: " + lootTableName);
                } catch (IOException e) {
                    getLogger().severe("Failed to load loot table from file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        File singlelootTableDir = new File(lootTableDir, "singles");

        files = singlelootTableDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()));
                    String lootTableName = file.getName().replace(".json", ""); // Get the file name without extension
                    lootTableRegistry.loadSingleLootTableFromJson(lootTableName, json);
                    getLogger().info("Loaded loot table: " + lootTableName);
                } catch (IOException e) {
                    getLogger().severe("Failed to load loot table from file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}
