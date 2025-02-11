package net.Vivelle.paperRegistryFramework.handlers;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent event){
        PaperRegistryFramework.killEffects(event.getPlayer());
    }
}
