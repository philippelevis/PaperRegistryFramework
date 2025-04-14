package net.Vivelle.paperRegistryFramework.recipes;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRegistry {
    private static List<PaperShapedRecipe> SHAPED = new ArrayList<>();

    public static PaperShapedRecipe getMatchingCraft(ItemStack[] craft){
        return SHAPED.stream().filter(recipe -> recipe.matches(craft)).findFirst().orElse(null);
    }

    public static void registerRecipe(PaperShapedRecipe recipe){
        SHAPED.add(recipe);
    }
    public static void unregisterRecipe(PaperShapedRecipe recipe){
        SHAPED.remove(recipe);
    }
}
