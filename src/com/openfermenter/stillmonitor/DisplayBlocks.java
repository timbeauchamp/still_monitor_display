package com.openfermenter.stillmonitor;

import java.util.ArrayList;

/**
 * Created by tbeauch on 7/17/15.
 */
public class DisplayBlocks
{
    private ArrayList<DisplayBlock> temps;
    private ArrayList<DisplayBlock> ranges;
    private ArrayList<DisplayBlock> switches;
    private ArrayList<DisplayBlock> all;

    public DisplayBlocks()
    {
        this.temps = new ArrayList<DisplayBlock>();
        this.ranges = new ArrayList<DisplayBlock>();
        this.switches = new ArrayList<DisplayBlock>();
        this.all = new ArrayList<DisplayBlock>();
    }

    public void addTemp(DisplayBlock temp)
    {
        temps.add(temp);
        all.add(temp);
    }

    public void addRange(DisplayBlock range)
    {
        ranges.add(range);
        all.add(range);
    }

    public void addSwitch(DisplayBlock aSwitch)
    {
        switches.add(aSwitch);
        all.add(aSwitch);
    }

    public ArrayList<DisplayBlock> getTemps()
    {
        return temps;
    }

    public ArrayList<DisplayBlock> getRanges()
    {
        return ranges;
    }

    public ArrayList<DisplayBlock> getSwitches()
    {
        return switches;
    }

    public ArrayList<DisplayBlock> getAll()
    {
        return all;
    }

    public DisplayBlock getDisplayBlock(String name)
    {

        for(DisplayBlock db : all)
        {
            if(db.getID().toLowerCase().contains(name))
                return db;
        }
        return null;
    }

}
