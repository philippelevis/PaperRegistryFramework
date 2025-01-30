package net.Vivelle.randomPaperFramework;

import net.Vivelle.randomPaperFramework.handlers.PlayerJoinListener;
import net.Vivelle.randomPaperFramework.loottables.LootTableRegistry;
import net.Vivelle.randomPaperFramework.util.MyEffect;
import net.Vivelle.randomPaperFramework.util.MyTickable;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class RandomPaperFramework extends JavaPlugin {
    public static NamespacedKey KarmaKey;
    public static ArrayList<MyTickable> Tickers = new ArrayList<>();
    public static HashMap<String, Class<? extends MyEffect>> Effects = new HashMap<>();
    public static LootTableRegistry lootTableRegistry;
    private static RandomPaperFramework instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        KarmaKey = new NamespacedKey(this, "karma");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        BukkitScheduler ticker = getServer().getScheduler();
        instance = this;

        lootTableRegistry = new net.Vivelle.randomPaperFramework.loottables.LootTableRegistry();

        ticker.runTaskTimer(this, ()->{
            ArrayList<net.Vivelle.randomPaperFramework.util.MyTickable> tickersCopy;
            synchronized (Tickers) {
                tickersCopy = new ArrayList<>(Tickers);
            }
            for (net.Vivelle.randomPaperFramework.util.MyTickable tickable : tickersCopy) {
                if (tickable != null) tickable.tick();
            }
        },0,1);

        createLootTableDirectory();
        loadLootTables();
    }

    public static int getKarma(Player player){
        return player.getPersistentDataContainer().getOrDefault(RandomPaperFramework.KarmaKey, PersistentDataType.INTEGER, 0);
    }
    public static void setKarma(Player player, int value){
        player.getPersistentDataContainer().set(RandomPaperFramework.KarmaKey, PersistentDataType.INTEGER, value);
    }

    public static void ApplyEffect(Class<? extends MyEffect> effect, Player plr) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            effect.getDeclaredConstructor(int.class, Player.class).newInstance(20*60, plr);
    }


    public static void RegisterEffect(String name, Class<? extends MyEffect> effect){
        Effects.put(name,effect);
    }

    public static Class<? extends MyEffect> getEffect(String name){
        Class<? extends net.Vivelle.randomPaperFramework.util.MyEffect> eff = Effects.get(name);
        if(eff != null){
            return eff;
        }else{
            throw new IllegalArgumentException("No valid effect found for key: " + name);
        }
    }

    public static LootTableRegistry getLootRegistry(){
        return lootTableRegistry;
    }

    public static void RemoveTicker(net.Vivelle.randomPaperFramework.util.MyTickable tickable){
        Tickers.remove(tickable);
    }
    public static void AddTicker(net.Vivelle.randomPaperFramework.util.MyTickable tickable){
        Tickers.add(tickable);
    }
    public static boolean HasTickable(net.Vivelle.randomPaperFramework.util.MyTickable tickable){
        return Tickers.contains(tickable);
    }

    public static RandomPaperFramework getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        List<net.Vivelle.randomPaperFramework.util.MyTickable> lst = new ArrayList<>(Tickers);
        lst.forEach(tickable->{
            if(tickable != null) {
                RemoveTicker(tickable);
                tickable.remove();
            }
        });

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
