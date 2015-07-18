package com.openfermenter.stillmonitor;

import java.util.prefs.Preferences;

/**
 * Created by tbeauch on 7/16/15.
 */
public class StillMonitorPreferences
{
    private static final String SOURCE_PREF = "source_pref";
    private Preferences prefs;
    private String sourceRef = "";

    public StillMonitorPreferences()
    {
        initializePreference();
    }

    public void initializePreference()
    {
        // This will define a node in which the preferences can be stored
        prefs = Preferences.userRoot().node(this.getClass().getName());
        loadPrefs();
    }

    public void loadPrefs()
    {
        sourceRef = prefs.get(SOURCE_PREF, "temps.xml");
    }

    public void savePrefs()
    {
        prefs.put(SOURCE_PREF, sourceRef);
    }

    public String getSourceRef()
    {
        return sourceRef;
    }

    public void setSourceRef(String in)
    {
        sourceRef = in;
    }
}
