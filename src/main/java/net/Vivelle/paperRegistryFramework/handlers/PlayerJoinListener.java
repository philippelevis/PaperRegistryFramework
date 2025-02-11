package net.Vivelle.paperRegistryFramework.handlers;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.Vivelle.paperRegistryFramework.util.MyEntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PlayerJoinListener implements Listener {
    protected NamespacedKey EffectListKey;
    protected NamespacedKey keyEffect;
    protected NamespacedKey keyLength;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");
        Player player = event.getPlayer();
        keyEffect = new NamespacedKey(PaperRegistryFramework.getInstance(), "reputation_effect");
        EffectListKey = new NamespacedKey(PaperRegistryFramework.getInstance(), "CustomEffects");
        keyLength = new NamespacedKey(PaperRegistryFramework.getInstance(), "EffectLength");
        if (player.getPersistentDataContainer().has(PaperRegistryFramework.KarmaKey, PersistentDataType.INTEGER)) {
            int karma = player.getPersistentDataContainer().get(PaperRegistryFramework.KarmaKey, PersistentDataType.INTEGER);
            player.sendMessage("Your karma is " + karma);
        }
        else {
            player.sendMessage("You have no karma");
            player.getPersistentDataContainer().set(PaperRegistryFramework.KarmaKey, PersistentDataType.INTEGER, 0);
            player.sendMessage("karma added successfully :3");
        }

        if(player.getPersistentDataContainer().has(EffectListKey)){
            List<PersistentDataContainer> lst = player.getPersistentDataContainer().get(EffectListKey, PersistentDataType.LIST.dataContainers());
            lst.forEach(cont ->{
                String effect = cont.getOrDefault(keyEffect,PersistentDataType.STRING,"NO EFFECT");
                int remaining = cont.getOrDefault(keyLength,PersistentDataType.INTEGER,0);
                Class<? extends MyEntityEffect> effect1 = PaperRegistryFramework.getEffect(effect);
                if(effect1 != null) {
                    try {
                        effect1.getDeclaredConstructor(int.class, Player.class).newInstance(remaining, player);
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                             IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
