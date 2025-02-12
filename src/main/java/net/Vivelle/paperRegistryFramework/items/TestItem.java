package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TestItem extends PaperItem{
    public TestItem(NamespacedKey name) {
        super(name);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        event.getPlayer().sendMessage("interacting using test");

    }

    @Override
    public void onTick(Entity entity) {
        if(entity instanceof Player plr){
            plr.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,200,3));
        }
    }
}
