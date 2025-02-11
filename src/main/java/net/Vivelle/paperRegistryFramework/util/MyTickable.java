package net.Vivelle.paperRegistryFramework.util;

import net.Vivelle.paperRegistryFramework.PaperRegistryFramework;

public class MyTickable{
    public MyTickable(){
        PaperRegistryFramework.AddTicker(this);
    }
    public void tick(){}

    public void remove(){}
}
