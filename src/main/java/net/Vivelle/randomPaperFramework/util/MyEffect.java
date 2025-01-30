package net.Vivelle.randomPaperFramework.util;

import net.Vivelle.randomPaperFramework.RandomPaperFramework;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * MyEffect is a custom effect class that is meant to be applicable to players only. each effect is stored in a players PDC.
 * to add a new effect extend this class, redefine the tick method and redefine the EffectName String.
 * to register the new effect utilize the PaperRegistryFramework#addEffect method with your new effect and its name.
 * <p>
 * To apply the newly added effect just run its constructor with length and the player you want to apply it to
 * alternatively you can utilize PaperRegistryFramework#applyEffect with this effects class and the player you want to apply it to.
 * </p>
 */
public abstract class MyEffect extends MyTickable{
    protected int length;
    protected NamespacedKey EffectListKey = new NamespacedKey(RandomPaperFramework.getInstance(), "CustomEffects");
    protected NamespacedKey keyEffect = new NamespacedKey(RandomPaperFramework.getInstance(), "reputation_effect");
    protected NamespacedKey keyLength = new NamespacedKey(RandomPaperFramework.getInstance(), "EffectLength");
    protected String EffectName;
    protected Player player;
    public MyEffect(int len, Player plr){
        EffectName = "noName";
        length = len;
        player = plr;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.length <= 0){
            this.remove();
        }
        if(this.length <= 20*60*60) {//if below an hour tick down, else assume infinite ;3
            this.length--;
        }
        save();
    }

    public void save(){
        List<PersistentDataContainer> immutableList = player.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        PersistentDataContainer me = RemoveMeInArray(lst);
        me.set(keyLength, PersistentDataType.INTEGER, length);
        me.set(keyEffect, PersistentDataType.STRING, this.EffectName);
        if(length > 0) {
            lst.add(me);
        }
        player.getPersistentDataContainer().set(EffectListKey, PersistentDataType.LIST.dataContainers(), lst);
    }
    protected PersistentDataContainer RemoveMeInArray(List<PersistentDataContainer> lst){
        for (int i=0;i<lst.size();i++) {
            PersistentDataContainer cont = lst.get(i);
            if(cont.getOrDefault(keyEffect, PersistentDataType.STRING, "").equals(EffectName)){
                lst.remove(cont);
                cont.set(keyLength,PersistentDataType.INTEGER,-1);
                return cont;
            }
        }
        return player.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
    }
    public void load(Player player){
        player.sendMessage("loading effect");
        List<PersistentDataContainer> immutableList = player.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        lst.forEach(cont -> {
            if(cont.has(keyEffect) && cont.get(keyEffect, PersistentDataType.STRING).equals(this.EffectName)){
                this.length = cont.getOrDefault(keyLength, PersistentDataType.INTEGER,-1);
                this.player = player;
            }
        });
        RandomPaperFramework.AddTicker(this);
    }

    @Override
    public void remove(){
        RandomPaperFramework.RemoveTicker(this);
        List<PersistentDataContainer> immutableList = player.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        RemoveMeInArray(lst);
        player.getPersistentDataContainer().set(EffectListKey, PersistentDataType.LIST.dataContainers(), lst);
    }
}
