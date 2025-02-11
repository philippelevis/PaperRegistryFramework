package net.Vivelle.paperRegistryFramework.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.NamespacedKey;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {
    private List<PaperShapedRecipe> recipes = new ArrayList<>();

    public void loadRecipes(String filePath) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(filePath)) {
            var jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (var jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                List<PaperShapedRecipe.Pair> ingredients = new ArrayList<>();

                // Parse ingredients
                var ingredientsJson = jsonObject.getAsJsonObject("ingredients");
                for (var entry : ingredientsJson.entrySet()) {
                    NamespacedKey key = NamespacedKey.fromString(entry.getKey());
                    int amount = entry.getValue().getAsInt();
                    ingredients.add(new PaperShapedRecipe.Pair(key, amount));
                }

                // Parse result
                NamespacedKey result = NamespacedKey.fromString(jsonObject.get("result").getAsString());

                // Create and add the recipe
                recipes.add(new PaperShapedRecipe(ingredients, result));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PaperShapedRecipe> getRecipes() {
        return recipes;
    }
}
