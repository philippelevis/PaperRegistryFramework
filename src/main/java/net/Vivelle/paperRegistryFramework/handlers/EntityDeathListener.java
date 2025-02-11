package net.Vivelle.paperRegistryFramework.handlers;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        PaperRegistryFramework.getInstance().getLogger().info(event.getEntity()+" killed");
        PaperRegistryFramework.killEffects(event.getEntity());
    }
}
