package net.Vivelle.paperRegistryFramework.handlers;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.Vivelle.paperRegistryFramework.items.PaperItem;
import net.Vivelle.paperRegistryFramework.items.PaperItemManager;
import net.Vivelle.paperRegistryFramework.recipes.PaperShapedRecipe;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PrepareCraftListener implements Listener {
    private final Map<Location, PaperShapedRecipe> recentRecipes = new HashMap<>();
    private final Map<Location, PaperItem> recentItems = new HashMap<>();

    private final PaperShapedRecipe test = new PaperShapedRecipe(
            List.of(new PaperShapedRecipe.Pair[]{
                    new PaperShapedRecipe.Pair(NamespacedKey.minecraft("iron_bars"), 3)
            }),new NamespacedKey(PaperRegistryFramework.getInstance(),"test")
    );

    @EventHandler
    public void CustomRecipeThing(PrepareItemCraftEvent event){
        if(event.getRecipe() == null){
            if(test.matches(event.getInventory().getMatrix())){
                if (recentItems.get(event.getInventory().getLocation())!=null)recentItems.get(event.getInventory().getLocation()).remove();
                recentRecipes.put(event.getInventory().getLocation(),test);
                ItemStack stack = test.getResultStack();
                event.getInventory().setResult(stack);
                recentItems.put(event.getInventory().getLocation(), PaperItemManager.getItem(stack));
            }
            //PaperRegistryFramework.getInstance().getLogger().info("preparing"+event.getInventory().getResult());
        }

    }
    @EventHandler
    public void Recipecrafted(InventoryClickEvent event){
        if(event.getInventory() instanceof CraftingInventory && event.getSlotType() == InventoryType.SlotType.RESULT) {
            PaperRegistryFramework.getInstance().getLogger().info("crafted" + event.getCurrentItem());
            PaperShapedRecipe recipe = recentRecipes.get(event.getInventory().getLocation());
            recentItems.remove(event.getInventory().getLocation());
            ItemStack[] lst = ((CraftingInventory) event.getInventory()).getMatrix();
            if (recipe != null) {
                for (int i = 0; i < lst.length; i++) {
                    ItemStack stack = lst[i];
                    if (stack != null && recipe.getIngredients().get(i) != null) {
                        stack.subtract(recipe.getIngredients().get(i).amount);
                    }
                }
            }
        }
    }
}
