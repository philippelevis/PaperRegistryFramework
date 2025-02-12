package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.Vivelle.paperRegistryFramework.util.MyEntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.awt.print.Paper;

public class TickItemsEffect extends MyEntityEffect {
    public static void reg(String name){
        EffectName = name;
        PaperRegistryFramework.RegisterEffect(name, TickItemsEffect.class);
    }

    public TickItemsEffect(int len, Entity ent) {
        super(20*60*120, ent);
    }

    @Override
    public void tick() {
        super.tick();
        //PaperRegistryFramework.getInstance().getLogger().info("tick");
        if(entity instanceof InventoryHolder holder){
            for (ItemStack stack : holder.getInventory()) {
                if (stack != null && PaperItemManager.isCustom(stack)){
                    PaperItem item = PaperItemManager.getTickingItem(stack);
                    if (item != null){
                        item.onTick(entity);
                    }
                }
            }
        }
    }
}
