package net.Vivelle.paperRegistryFramework.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.awt.print.Paper;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {
    public static void loadRecipes(String filePath) {
        Gson gson = new Gson();
        PaperRegistryFramework.getInstance().getLogger().info("loading recipes");

        try (FileReader reader = new FileReader(filePath)) {
            var jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (var jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                List<PaperShapedRecipe.Pair> ingredients = new ArrayList<>();

                // Parse ingredients
                var ingredientsJson = jsonObject.getAsJsonArray("ingredients");
                for (var entry : ingredientsJson) {
                    for (String skey : entry.getAsJsonObject().keySet()){
                        PaperRegistryFramework.getInstance().getLogger().info(entry.toString());
                        NamespacedKey key = NamespacedKey.fromString(skey);
                        int amount = entry.getAsJsonObject().get(skey).getAsInt();
                        ingredients.add(new PaperShapedRecipe.Pair(key, amount));
                    }
                }

                // Fill ingredients up to 9 with Material.AIR if necessary
                while (ingredients.size() < 9) {
                    ingredients.add(new PaperShapedRecipe.Pair(Material.AIR.getKey(), 1));
                }
                PaperRegistryFramework.getInstance().getLogger().info(ingredients.toString());

                // Parse result
                NamespacedKey result = NamespacedKey.fromString(jsonObject.get("result").getAsString());
                PaperShapedRecipe recipe = new PaperShapedRecipe(ingredients, result);
                PaperRegistryFramework.getInstance().getLogger().info(recipe.toString());
                // Create and add the recipe
                RecipeRegistry.registerRecipe(recipe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
