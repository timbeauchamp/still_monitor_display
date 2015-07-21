package com.openfermenter.stillmonitor;

import com.openfermenter.stillmonitor.com.openfermenter.stillmonitor.ui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main extends Application
{
    // public static String dataURL = "http://localhost://temps.xml";
    public static String dataURL = "http://192.168.1.15/temps.xml";
//    Text updateTime;
    RemoteData rd = new RemoteData();
    Timer timer;
    public static StillMonitorPreferences prefs;
    public static DisplayBlocks dBs;

    private Stage primaryStage;

    private static MainUI mainUI;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        prefs = new StillMonitorPreferences();
        dataURL = prefs.getSourceRef();

        dBs = buildDisplayBlocks();

        mainUI = new MainUI(primaryStage, this);
        //buildUI(primaryStage);

        primaryStage.setScene(mainUI.getScene());
        primaryStage.show();

        DataRetrievalService service = new DataRetrievalService();
        service.setUrl(dataURL);
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent t)
            {
                RemoteData rd = (RemoteData) t.getSource().getValue();
                System.out.println("done:" + rd.toString());
                mainUI.updateUI(rd);
            }
        });
        service.start();
        startUpdater();

    }

    @Override
    public void stop()
    {
        timer.cancel();
    }

    public void showAssigner()
    {
        NodeAssignerUI na = new NodeAssignerUI(primaryStage, this);
        primaryStage.setScene(na.getScene());
        primaryStage.show();
    }

    public void showMainUI()
    {
        primaryStage.setScene(mainUI.getScene());
        primaryStage.show();

    }

    public void startUpdater()
    {
        timer = new Timer();

        timer.schedule(new TimerTask()
        {
            public void run()
            {
                Platform.runLater(new Runnable()
                {
                    public void run()
                    {
                        System.out.println("Updating from: " + dataURL);
                        mainUI.updateUI(doc2RemoteData(getData(dataURL)));
                    }
                });
            }
        }, 5000, 10000);
    }

    private DisplayBlocks buildDisplayBlocks()
    {
        dBs = new DisplayBlocks();

        dBs.addTemp(new DisplayBlockTemp("Temp 0",1,3));
        dBs.addTemp(new DisplayBlockTemp("Temp 1",1,4));
        dBs.addTemp(new DisplayBlockTemp("Temp 2",1,5));
        dBs.addTemp(new DisplayBlockTemp("Temp 3",1,6));

        dBs.addSwitch(new DisplayBlockRange("Range 0", 2, 3));
        dBs.addRange(new DisplayBlockRange("Range 1", 2, 4));
        dBs.addRange(new DisplayBlockRange("Range 2", 2, 5));
        dBs.addRange(new DisplayBlockRange("Range 3", 2, 6));

        dBs.addSwitch(new DisplayBlock("Switch 0", 3, 3));
        dBs.addSwitch(new DisplayBlock("Switch 1", 3, 4));
        dBs.addSwitch(new DisplayBlock("Switch 2", 3, 5));
        dBs.addSwitch(new DisplayBlock("Switch 3", 3, 6));

        return dBs;
    }


    private void showTargetChooser()
    {
        Dialog dialog = new Dialog();

        dialog.showAndWait()
                .filter(response -> response == ButtonType.OK);
//                .ifPresent(response -> formatSystem());
    }

    private static javafx.scene.Node getTempNode(String  name)
    {
        return new Label(name);
    }

    private static Document getData(String urlStr)
    {
        URL url;
        Document doc = null;
        try
        {
            if(!urlStr.startsWith("http"))
            {
                try
                {
                    urlStr = (new java.io.File(dataURL)).toURI().toURL().toString();
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }

            url = new URL(urlStr);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try
        {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            doc = db.parse(url.openStream());
        } catch (SAXException|IOException e)
        {
            e.printStackTrace();
        }

        return doc;
    }

    private static RemoteData doc2RemoteData(Document doc)
    {

        RemoteData rd = new RemoteData();
        NodeList currentNodeList = doc.getElementsByTagName("temp");
        int len = currentNodeList.getLength();

        Node currentNode;
        String nodeName;
        String nodeVal;
        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeName = "temp " + currentNode.getAttributes().getNamedItem("id").getNodeValue();
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.temps.put(nodeName, Double.parseDouble(nodeVal));
        }

        currentNodeList = doc.getElementsByTagName("range");
        len = currentNodeList.getLength();

        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeName = "range " + currentNode.getAttributes().getNamedItem("id").getNodeValue();
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.ranges.put(nodeName, Double.parseDouble(nodeVal));
        }

        currentNodeList = doc.getElementsByTagName("switch");
        len = currentNodeList.getLength();

        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeName = "switch " + currentNode.getAttributes().getNamedItem("id").getNodeValue();
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.switches.put(nodeName, Boolean.parseBoolean(nodeVal));
        }
        return rd;
     }


    private static class DataRetrievalService extends Service<RemoteData>
    {
        private StringProperty url = new SimpleStringProperty();

        public final void setUrl(String value)
        {
            url.set(value);
        }

        public final String getUrl()
        {
            return url.get();
        }

        public final StringProperty urlProperty()
        {
            return url;
        }

        @Override
        protected Task<RemoteData> createTask()
        {
            return new Task<RemoteData>()
            {
                @Override
                protected RemoteData call() throws IOException, MalformedURLException
                {
                    return doc2RemoteData(getData(getUrl()));
                }
            };
        }
    }

}
