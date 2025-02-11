package net.Vivelle.paperRegistryFramework.items;

import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestItem extends PaperItem{
    public TestItem(NamespacedKey name) {
        super(name);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        event.getPlayer().sendMessage("interacting using test");
    }
}
