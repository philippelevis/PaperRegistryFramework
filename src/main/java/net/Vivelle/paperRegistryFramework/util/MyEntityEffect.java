package net.Vivelle.paperRegistryFramework.util;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MyEntityEffect extends MyTickable{
    protected int length;
    protected NamespacedKey EffectListKey = new NamespacedKey(PaperRegistryFramework.getInstance(), "CustomEffects");
    protected NamespacedKey keyEffect = new NamespacedKey(PaperRegistryFramework.getInstance(), "effect");
    protected NamespacedKey keyLength = new NamespacedKey(PaperRegistryFramework.getInstance(), "EffectLength");
    protected static String EffectName;
    protected Entity entity;
    public MyEntityEffect(int len, Entity ent){
        EffectName = "noName";
        length = len;
        entity = ent;
        if(!PaperRegistryFramework.getAppliedEffects().containsKey(ent)) PaperRegistryFramework.getAppliedEffects().put(ent,new ArrayList<MyEntityEffect>());
        PaperRegistryFramework.getAppliedEffects().get(ent).add(this);
    }

    public static String getEffectName() {
        return EffectName;
    }
    public String getEffectName(Object instance) {
        return EffectName;
    }

    public Entity getEntity() {
        return entity;
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
        List<PersistentDataContainer> immutableList = entity.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        PersistentDataContainer me = RemoveMeInArray(lst);
        me.set(keyLength, PersistentDataType.INTEGER, length);
        me.set(keyEffect, PersistentDataType.STRING, this.EffectName);
        if(length > 0) {
            lst.add(me);
        }
        entity.getPersistentDataContainer().set(EffectListKey, PersistentDataType.LIST.dataContainers(), lst);
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
        return entity.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
    }
    public void load(Entity entity){
        List<PersistentDataContainer> immutableList = entity.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        lst.forEach(cont -> {
            if(cont.has(keyEffect) && cont.get(keyEffect, PersistentDataType.STRING).equals(this.EffectName)){
                this.length = cont.getOrDefault(keyLength, PersistentDataType.INTEGER,-1);
                this.entity = entity;
            }
        });
        PaperRegistryFramework.AddTicker(this);
    }

    @Override
    public void remove(){
        PaperRegistryFramework.RemoveTicker(this);
        List<PersistentDataContainer> immutableList = entity.getPersistentDataContainer().getOrDefault(EffectListKey, PersistentDataType.LIST.dataContainers(), new ArrayList<>());
        ArrayList<PersistentDataContainer> lst = new ArrayList<>(immutableList);
        RemoveMeInArray(lst);
        entity.getPersistentDataContainer().set(EffectListKey, PersistentDataType.LIST.dataContainers(), lst);
    }
}
