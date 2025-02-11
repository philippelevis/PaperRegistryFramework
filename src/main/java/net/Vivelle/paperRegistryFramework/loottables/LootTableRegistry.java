package net.Vivelle.paperRegistryFramework.loottables;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LootTableRegistry {
    private final Map<String, LootTable> lootTables;
    private final Map<String, SingleLootTable> singleLootTables;

    public LootTableRegistry() {
        this.lootTables = new HashMap<>();
        this.singleLootTables = new HashMap<>();
    }

    public void registerSingle(String name, SingleLootTable table){
        singleLootTables.put(name, table);
    }

    public boolean hasSingle(String name){
        return singleLootTables.containsKey(name);
    }

    public SingleLootTable getSingle(String name){
        return singleLootTables.get(name);
    }

    public void loadSingleLootTableFromJson(String name, String json) {
        SingleLootTable lootTable = new SingleLootTable();
        lootTable.loadFromJson(json);
        registerSingle(name, lootTable);
    }

    public void registerLootTable(String name, LootTable lootTable) {
        lootTables.put(name, lootTable);
    }

    public boolean hasLootTable(String name){
        return lootTables.containsKey(name);
    }

    public LootTable getLootTable(String name) {
        return lootTables.get(name);
    }

    public void loadLootTableFromJson(String name, String json) {
        PaperRegistryFramework.getInstance().getLogger().log(Level.WARNING,json);
        LootTable lootTable = new LootTable();
        lootTable.loadFromJson(json);
        registerLootTable(name, lootTable);
    }
}

