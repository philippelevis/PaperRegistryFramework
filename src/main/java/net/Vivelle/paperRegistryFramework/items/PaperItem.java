package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.print.Paper;
import java.lang.reflect.Method;
import java.util.*;

import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.ID_KEY;
import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.UUID_KEY;


public class PaperItem implements Listener {
    protected static Material DEFAULT_MAT = Material.DIAMOND;
    protected static int DEFAULT_AMOUNT = 1;
    protected static int DEFAULT_DURA = 100;

    private ItemStack itemStack;
    private final int maxdurability;
    private final UUID uuid;

    public PaperItem(NamespacedKey name){
        this(DEFAULT_MAT,DEFAULT_AMOUNT,DEFAULT_DURA,name);
    }



    public PaperItem(Material fake, int amount, int maxdurability,NamespacedKey name){
        this.itemStack = new ItemStack(fake);
        this.uuid = UUID.randomUUID();
        this.itemStack.setAmount(amount);
        itemStack.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(UUID_KEY, PersistentDataType.STRING, this.uuid.toString());
            pdc.set(ID_KEY, PersistentDataType.STRING, name.value());
        });
        this.maxdurability = maxdurability;
        PaperItemManager.createItem(this);
    }

    public UUID getUUID(){
        return uuid;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void updateItemStack(ItemStack stack){
        itemStack = stack;
        itemStack.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(UUID_KEY, PersistentDataType.STRING,this.uuid.toString());
        });
    }

    public Material getMaterial() {
        return itemStack.getType();
    }
    /** Invoker. used to invoke onUse method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onInteractInvoker(PlayerInteractEvent event){
        try {
            if(this.equals(event.getItem())){
                this.onUse(event);
            }
        }catch (Exception ignored){}
    }
    /** Invoker. used to invoke onDrop method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onDropInvoker(PlayerDropItemEvent event){
        try {
            if (this.equals(event.getItemDrop().getItemStack())) {
                this.onDrop(event);
            }
        }catch (Exception ignored){}
    }
    /** Invoker. used to invoke onCraft method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onCraftInvoker(CraftItemEvent event){//how do i make the item have the uuid tag when its crafted... perhaps a customization during PrepareItemCraft event and create a paperitem without init?????
        try {
            if (this.equals(event.getCurrentItem())) {
                this.onCraft(event);
            }
        } catch (Exception ignored) {
        }
    }

    /** Define what happens when the item is used**/
    public void onUse(PlayerInteractEvent event) {

    }

    /** Define what happens when the item is dropped**/
    public void onDrop(PlayerDropItemEvent event) {
    }

    /** Define what happens when the item is crafted**/
    public void onCraft(CraftItemEvent event) {

    }

    /** Define what happens every tick.
     * <p>
     * If not overriden, does not tick, checks for being overriden with {@link Method#equals(Object)}
     */
    public void onTick(){

    }

    /** Define how damaging the item works**/
    public void damage(int amount, LivingEntity lent){
        this.itemStack = this.itemStack.damage(amount,lent);
        if(this.itemStack == ItemStack.empty()){
            this.remove();
        }
    }

    /**Used to check if this custom item instance is tied to a certain ItemStack.
     * <p> returns true if so, false otherwise**/
    public boolean equals(ItemStack item){
        if(this.itemStack == ItemStack.empty()){
            this.remove();
            return false;
        }
        PaperRegistryFramework.getInstance().getLogger().info(item.getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"none"));
        return this.itemStack == item || UUID.fromString(item.getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"")).equals(uuid);
    }

    /**removal method. use when you want to untie a PaperItem from an ItemStack. To remove an item fully use {@link #removeFully()}**/
    public void remove(){
        PaperItemManager.removeItem(this);
    }

    public void setName(String name){
        itemStack.getItemMeta().itemName(Component.text(name));
    }

    public void setLore(List<String> lore){
        List<Component> lore2 = new ArrayList<>(lore.stream().map(Component::text).toList());
        itemStack.getItemMeta().lore(lore2);
    }

    /**removes the item and its corresponding ItemStack. To leave the ItemStack use {@link #remove()}**/
    public void removeFully(){
        this.itemStack.setAmount(0);
        this.remove();
        PlayerDropItemEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        CraftItemEvent.getHandlerList().unregister(this);
    }
}
