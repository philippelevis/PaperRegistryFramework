package net.Vivelle.paperRegistryFramework.items;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.print.Paper;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;

import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.ID_KEY;
import static net.Vivelle.paperRegistryFramework.items.PaperItemManager.UUID_KEY;

public class PaperItem implements Listener {
    protected static Material DEFAULT_MAT = Material.DIAMOND;
    protected static int DEFAULT_AMOUNT = 1;
    protected static int DEFAULT_DURA = 100;
    private WeakReference<ItemStack> itemStack;

    //private ItemStack itemStack;
    private final int maxdurability;
    private final UUID uuid;

    public PaperItem(NamespacedKey name){
        this(name,DEFAULT_AMOUNT);
    }
    public PaperItem(NamespacedKey name, int amt){
        this(PaperItemManager.getItemMat(name),amt,DEFAULT_DURA,name);
        PaperRegistryFramework.getInstance().getLogger().info("creating item");
    }

    public PaperItem(Material fake, int amount, int maxdurability,NamespacedKey name){
        if (amount > 1){
            throw new NotImplementedException("amount cannot be more than 1 for now, its being worked on \n" +
                    "any help on figuring out stack splitting will be great!");

        }
        ItemStack itemStack = new ItemStack(fake);
        this.itemStack = new WeakReference<>(itemStack);
        this.uuid = UUID.randomUUID();
        itemStack.setAmount(amount);
        itemStack.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(UUID_KEY, PersistentDataType.STRING, this.uuid.toString());
            pdc.set(ID_KEY, PersistentDataType.STRING, name.toString());
        });
        this.maxdurability = maxdurability;
        PaperItemManager.createItem(this);

    }
    /**A constructor for loading an instance of PaperItem from an itemStack. Check if it has an id first :3
     * <p><font color="yellow">WARNING: replaces the items UUID! save everything in the item!</font></p>**/
    public PaperItem(ItemStack load){
        this.itemStack = new WeakReference<>(load);
        this.uuid=UUID.randomUUID();
        this.maxdurability = load.getDurability();
        load.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(UUID_KEY, PersistentDataType.STRING, this.uuid.toString());
        });
        PaperItemManager.createItem(this);
    }

    public UUID getUUID(){
        return uuid;
    }

    public ItemStack getItemStack() {
        if(itemStack.get() != null)
            return itemStack.get();
        else {
            this.remove();
            return ItemStack.empty();
        }
    }

    public void updateItemStack(ItemStack stack){
        itemStack = new WeakReference<>(stack);
        stack.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(UUID_KEY, PersistentDataType.STRING,this.uuid.toString());
        });
    }

    public Material getMaterial() {
        return getItemStack().getType();
    }
    /** Invoker. used to invoke onUse method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onInteractInvoker(PlayerInteractEvent event){
        try {
            if(this.equals(event.getItem())){
                if(event.getAction().isRightClick()) {
                    this.onUse(event);
                } else if (event.getAction().isLeftClick()) {
                    this.onAttack(event);
                }
            }
        }catch (Exception ignored){}
    }
    /** Invoker. used to invoke onUse method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onEntityInteract(PlayerInteractEntityEvent event){
        try {
            if(this.equals(event.getPlayer().getInventory().getItem(event.getHand()))){
                this.onUse(event);
            }
        }catch (Exception ignored){}
    }

    @EventHandler
    public final void InventoryClickInvoker(InventoryClickEvent e){
        try{
            if(this.equals(e.getCurrentItem())){
                if(!e.getCurrentItem().equals(this.getItemStack())){
                    PaperItemManager.loadItem(e.getCurrentItem());
                }
            }
        }catch(Exception ignored){}
    }

    /** Invoker. used to invoke onDrop method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onDropInvoker(PlayerDropItemEvent event){
        try {
            if (this.equals(event.getItemDrop().getItemStack())) {
                if(!event.getItemDrop().getItemStack().equals(this.getItemStack())){
                    PaperItemManager.loadItem(event.getItemDrop().getItemStack());
                }
                this.onDrop(event);
            }
        }catch (Exception ignored){}
    }
    /** Invoker. used to invoke onCraft method. dont override, override invoked methods instead.**/
    @EventHandler
    public final void onCraftInvoker(CraftItemEvent event){
        try {
            if (this.equals(event.getCurrentItem())) {
                this.onCraft(event);
            }
        } catch (Exception ignored) {
        }
    }

    public void keepAlive(){
        this.equals(new ItemStack(Material.AIR));
    }

    /** Define what happens when the item is used**/
    public void onUse(Event event) {

    }

    /** Define what happens when something is attacked with this item**/
    public void onAttack(PlayerInteractEvent event){

    }

    /** Define what happens when the item is dropped**/
    public void onDrop(PlayerDropItemEvent event) {
    }

    /** Define what happens when the item is crafted**/
    public void onCraft(CraftItemEvent event) {

    }

    /** Define what happens every tick.
     * <p>
     * If not overriden, does not tick, checks for being overriden with {@link PaperItemManager#hasOnTick(Class)}
     */
    public void onTick(Entity entity){

    }

    /** Define how damaging the item works**/
    public void damage(int amount, LivingEntity lent){
        ItemStack stack = getItemStack();
        stack = stack.damage(amount,lent);
        if(stack == ItemStack.empty()){
            this.remove();
        }
    }

    /**Used to check if this custom item instance is tied to a certain ItemStack.
     * <p> returns true if so, false otherwise**/
    public boolean equals(ItemStack item){
        ItemStack stack = this.itemStack.get();
        if(stack == null || stack == ItemStack.empty()){
            this.remove();
            return false;
        }
        PaperRegistryFramework.getInstance().getLogger().info(item.getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"none"));
        return UUID.fromString(item.getItemMeta().getPersistentDataContainer().getOrDefault(UUID_KEY, PersistentDataType.STRING,"")).equals(uuid);
    }

    /**removal method. use when you want to untie a PaperItem from an ItemStack. To remove an item fully use {@link #removeFully()}
     **/
    public void remove(){
        PaperRegistryFramework.getInstance().getLogger().info("removing self");
        PaperItemManager.removeItem(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        CraftItemEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
    }

    public void setName(String name){
        getItemStack().getItemMeta().itemName(Component.text(name));
    }

    public void setLore(List<String> lore){
        List<Component> lore2 = new ArrayList<>(lore.stream().map(Component::text).toList());
        getItemStack().getItemMeta().lore(lore2);
    }

    /**removes the item and its corresponding ItemStack. To leave the ItemStack use {@link #remove()}**/
    public void removeFully(){
        getItemStack().setAmount(0);
        this.remove();
    }
}
