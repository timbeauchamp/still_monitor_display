package com.openfermenter.stillmonitor;

import java.util.Hashtable;

/**
 * Created by tbeauch on 7/20/15.
 */
public class RemoteData
{
    public Hashtable<String, Double> temps = new Hashtable<String, Double>();
    public Hashtable<String, Double> ranges = new Hashtable<String, Double>();
    public Hashtable<String, Boolean> switches = new Hashtable<String, Boolean>();

    @Override
    public String toString()
    {
        return "Num Temps:"+ temps.size() + "  Num Ranges:"+ ranges.size() + "  Num Swithces: "+ switches.size();
    }
}
