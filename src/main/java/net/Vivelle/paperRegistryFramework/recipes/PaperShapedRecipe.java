package net.Vivelle.paperRegistryFramework.recipes;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.Vivelle.paperRegistryFramework.items.PaperItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PaperShapedRecipe {
    private final List<Pair> ingredients; // Map of ingredient types and their required amounts
    private final NamespacedKey result; // Resulting item
    private final int amount;

    public PaperShapedRecipe(List<Pair> ingredients, NamespacedKey result){
        this(ingredients,result,1);
    }

    public PaperShapedRecipe(List<Pair> ingredients, NamespacedKey result, int amount) {
        this.ingredients = ingredients;
        this.result = result;
        this.amount = amount;
    }

    public List<Pair> getIngredients() {
        return ingredients;
    }

    public ItemStack getResultStack(){
        Material mat = Material.matchMaterial(result.toString());
        if(mat != null) return new ItemStack(mat,amount);
        else return PaperItemManager.instanceItem(result);
    }

    public NamespacedKey getResult() {
        return result;
    }


    public boolean matches(ItemStack[] craft) {
        List<NamespacedKey> lst = new ArrayList<>();
        for (int i = 0; i < craft.length; i++) {
            if (ingredients.size() < i + 1) continue;
            ItemStack stack = craft[i];
            NamespacedKey key = (stack != null) ? PaperItemManager.getItemName(stack) : Material.AIR.getKey();
            if (!ingredients.get(i).item.equals(key) || (stack != null && ingredients.get(i).amount > stack.getAmount())) {
                return false;
            } else {
                lst.add(key);
            }
        }
        return lst.size() == ingredients.size();
    }

//    public boolean matches(ItemStack[] craft){
//
//
//
//        List<NamespacedKey> lst = new ArrayList<>();
//        PaperRegistryFramework.getInstance().getLogger().info("checking match with "+ingredients.toString());
//        for (int i=0;i<craft.length;i++) {
//            if(ingredients.size()<i+1) continue;
//            ItemStack stack = craft[i];
//            if(stack != null) {
//                NamespacedKey key = PaperItemManager.getItemName(stack);
//                if (!ingredients.get(i).item.equals(key) || ingredients.get(i).amount > stack.getAmount() ) {
//                    PaperRegistryFramework.getInstance().getLogger().info(ingredients.get(i).item+" != "+key+" or "+ingredients.get(i).amount+" != "+stack.getAmount());
//                    lst.add(key);
//                    return false;
//                }else {
//                    lst.add(key);
//                    PaperRegistryFramework.getInstance().getLogger().info(lst.toString());
//                }
//            }else{
//                lst.add(Material.AIR.getKey());
//            }
//        }
//        return lst.size() == ingredients.size();
//    }


    public static class Pair{
        public NamespacedKey item;
        public int amount;
        public Pair(NamespacedKey item,int amount){
            this.item=item;
            this.amount=amount;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "item=" + item +
                    ", amount=" + amount +
                    '}';
        }
    }
}
