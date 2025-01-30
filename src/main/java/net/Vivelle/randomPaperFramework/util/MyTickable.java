package net.Vivelle.randomPaperFramework.util;

import net.Vivelle.randomPaperFramework.RandomPaperFramework;

public class MyTickable{
    public MyTickable(){
        RandomPaperFramework.AddTicker(this);
    }
    public void tick(){}

    public void remove(){}
}
