package com.openfermenter.stillmonitor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.prefs.Preferences;

/**
 * Created by tbeauch on 7/16/15.
 */
public class StillMonitorPreferences
{
    private static final String SOURCE_PREF = "source_pref";
    private static final String SOURCE_SELECT = "source_select";

    private Preferences prefs;
    private String sourceRef = "";
    private String sourceSelect = "raspberry";

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
        sourceRef = prefs.get(SOURCE_PREF, "http://192.168.1.15/temps.xml");
        sourceSelect = prefs.get(SOURCE_SELECT, "raspberry");
    }

    public void savePrefs()
    {
        prefs.put(SOURCE_PREF, sourceRef);
        prefs.put(SOURCE_SELECT, sourceSelect);
    }

    public String getSourceRef()
    {
        return sourceRef;
    }

    public void setSourceRef(String in)
    {
        sourceRef = in;
    }

    public void setSourceSelect(String in)
    {
        sourceSelect = in;
        if(in.equalsIgnoreCase("raspberry"))
        {
            sourceRef = "http://192.168.1.15/temps.xml";
        }
        else if(in.equalsIgnoreCase("localhost"))
        {
            sourceRef = "http://localhost/temps.xml";

        }
        else if(in.equalsIgnoreCase("localfile"))
        {
//            sourceRef = "file://localhost/Users/tbeauch/IdeaProjects/still_monitor_display/temps.xml";


            sourceRef = "temps.xml";
//            PrintWriter writer = null;
//            try
//            {
//                writer = new PrintWriter("the-file-name.txt", "UTF-8");
//            } catch (FileNotFoundException e)
//            {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e)
//            {
//                e.printStackTrace();
//            }
//            writer.println("The first line"
//            );
//            writer.println("The second line");
//            writer.close();
//
//            String current = null;
//            try
//            {
//                current = new java.io.File( "." ).getCanonicalPath();
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//            System.out.println("Current dir:"+current);
//            String currentDir = System.getProperty("user.dir");
//            System.out.println("Current dir using System:" + currentDir);
        }
        else
        {
            sourceRef = "http://192.168.1.15/temps.xml";
        }
    }

    public String getSourceSelect()
    {
        return sourceSelect;
    }
}
