package net.Vivelle.randomPaperFramework.loottables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.Vivelle.randomPaperFramework.loottables.LootTable.*;

public class SingleLootTable {

    private final List<LootItem> lootItems;
    private final Random random;

    public SingleLootTable() {
        this.lootItems = new ArrayList<>();
        this.random = new Random();
    }

    // Method to add an item to the loot table
    public void addItem(String name, int weight) {
        lootItems.add(new LootItem(name, weight));
    }

    // Method to get a single random loot item based on weights
    public String getSingleLoot() {
        if (lootItems.isEmpty()) {
            return null; // No items to choose from
        }

        int totalWeight = 0;
        for (LootItem item : lootItems) {
            totalWeight += item.weight;
        }

        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (LootItem item : lootItems) {
            cumulativeWeight += item.weight;
            if (randomValue < cumulativeWeight) {
                return item.name; // Return the selected item
            }
        }

        return null; // This should never happen if the loot table is not empty
    }

    // Method to display the loot table (for debugging purposes)
    public String toString() {
        String res = "";
        res += ("Single Loot Table:{");
        for (LootItem item : lootItems) {
            res += ("{" + item.name + ",weight:" + item.weight+"}");
        }
        res += "}";
        return res;
    }

    public void loadFromJson(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");

        for (JsonElement element : itemsArray) {
            JsonObject itemObject = element.getAsJsonObject();
            String name = itemObject.get("name").getAsString();
            int weight = itemObject.get("weight").getAsInt();
            addItem(name, weight);
        }
    }
}
