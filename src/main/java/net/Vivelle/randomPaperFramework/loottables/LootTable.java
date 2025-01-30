package net.Vivelle.randomPaperFramework.loottables;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.Vivelle.randomPaperFramework.RandomPaperFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a loot table that contains multiple loot items with associated weights.
 * The loot table allows for adding items, selecting random loot based on weights,
 * and loading items from a JSON string.
 */

public class LootTable {

    static class LootItem {
        String name;
        int weight; // Higher weight means higher chance of being selected

        LootItem(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }

    private final List<LootItem> lootItems;
    private final Random random;

    public LootTable() {
        this.lootItems = new ArrayList<>();
        this.random = new Random();
    }

    // Method to add an item to the loot table
    public void addItem(String name, int weight) {
        lootItems.add(new LootItem(name, weight));
    }

    public String[] getRandomLoot(int numberOfItems) {
        List<String> selectedLoot = new ArrayList<>();
        int totalWeight = 0;
        // Calculate total weight
        for (LootItem item : lootItems) {
            totalWeight += item.weight;
        }

        // Check if total weight is zero
        if (totalWeight <= 0) {
            return selectedLoot.toArray(new String[0]); // Return empty array if no loot
        }

        // Select items based on weights
        for (int i = 0; i < numberOfItems; i++) {
            int randomValue = random.nextInt(totalWeight);
            int cumulativeWeight = 0;

            for (LootItem item : lootItems) {
                cumulativeWeight += item.weight;
                if (randomValue < cumulativeWeight) {
                    RandomPaperFramework.getInstance().getLogger().info(item.name+"; "+i);
                    selectedLoot.add(item.name); // Add the selected item to the list
                    break; // Exit the inner loop once an item is selected
                }
            }
        }

        return selectedLoot.toArray(new String[0]);
    }

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