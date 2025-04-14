package net.Vivelle.paperRegistryFramework.handlers;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.Vivelle.paperRegistryFramework.items.PaperItem;
import net.Vivelle.paperRegistryFramework.items.PaperItemManager;
import net.Vivelle.paperRegistryFramework.items.TickItemsEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.ID_KEY;
import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.UUID_KEY;

public class PaperItemMgrHelper implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity() instanceof Item item){
            if(item.getItemStack().getItemMeta() != null) {
                PersistentDataContainer pdc = item.getItemStack().getItemMeta().getPersistentDataContainer();
                if (pdc != null && (pdc.get(ID_KEY, PersistentDataType.STRING) != null && PaperItemManager.getItem(pdc.get(UUID_KEY, PersistentDataType.STRING)) != null)) {
                    PaperItemManager.getItem(pdc.get(UUID_KEY, PersistentDataType.STRING)).remove();
                }
            }
        }
    }
    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event){
        if (event.getEntity() instanceof Item item && item.getItemStack().getItemMeta() != null){
            PersistentDataContainer pdc = item.getItemStack().getItemMeta().getPersistentDataContainer();
            if(pdc.get(ID_KEY, PersistentDataType.STRING) != null && PaperItemManager.getItem(pdc.get(UUID_KEY,PersistentDataType.STRING))!=null){
                PaperItemManager.getItem(pdc.get(UUID_KEY,PersistentDataType.STRING)).remove();
            }
        }
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
        Inventory inv = event.getPlayer().getInventory();
        for (ItemStack stack : inv) {
            if (stack != null) {
                PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
                if (pdc.get(ID_KEY, PersistentDataType.STRING) != null && PaperItemManager.getItem(pdc.get(UUID_KEY, PersistentDataType.STRING)) != null) {
                    PaperItemManager.getItem(pdc.get(UUID_KEY, PersistentDataType.STRING)).remove();
                }
            }
        }
    }
    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event){
        try {
            PaperRegistryFramework.getInstance().getLogger().info("applying effect");
            PaperRegistryFramework.ApplyEffect(PaperRegistryFramework.getEffect(TickItemsEffect.getEffectName()), event.getPlayer());
        }catch (Exception e){
            PaperRegistryFramework.getInstance().getLogger().log(Level.WARNING,e.toString());
        }
        Inventory inv = event.getPlayer().getInventory();
        for (ItemStack stack : inv) {
            if (stack != null) {
                PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
                if (pdc.get(ID_KEY, PersistentDataType.STRING) != null && PaperItemManager.getItem(pdc.get(UUID_KEY, PersistentDataType.STRING)) != null) {
                    PaperItemManager.loadItem(stack);
                }
            }
        }
    }
}
