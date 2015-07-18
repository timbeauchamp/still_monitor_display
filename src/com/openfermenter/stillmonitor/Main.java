package com.openfermenter.stillmonitor;

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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main extends Application
{
    // public static String dataURL = "http://localhost://temps.xml";
    public static String dataURL = "http://192.168.1.15/temps.xml";
    Text updateTime;
    RemoteData rd = new RemoteData();
    Timer timer;
    StillMonitorPreferences prefs;
    DisplayBlocks dBs;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        prefs = new StillMonitorPreferences();
        dataURL = prefs.getSourceRef();

        dBs = buildDisplayBlocks();

        buildUI(primaryStage);

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
                updateUI(rd);
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
                        updateUI(doc2RemoteData(getData(dataURL)));
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

    private void buildUI(Stage primaryStage)
    {
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Still Monitor Display");


        GridPane grid = new GridPane();
//        grid.setStyle("-fx-background-color: #336699;");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        Scene scene = new Scene(border, 600, 475);


        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuTarget = new Menu("Target");

        String targetString = prefs.getSourceSelect();
        ToggleGroup tg = new ToggleGroup();
        // --- Creating check menu items
        RadioMenuItem localhostTarget = createMenuItem ("Localhost", "localhost", tg, targetString.equalsIgnoreCase("localhost"));
        RadioMenuItem localfileTarget = createMenuItem ("Local File", "localfile", tg, targetString.equalsIgnoreCase("localfile"));
        RadioMenuItem raspberryTarget = createMenuItem ("Raspberry", "raspberry", tg, targetString.equalsIgnoreCase("raspberry"));
        menuTarget.getItems().addAll(localhostTarget, localfileTarget, raspberryTarget);



        MenuItem targetMenuItem = new MenuItem("Change Target");
        targetMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t)
            {
//                showTargetChooser();
            }
        });

        menuEdit.getItems().addAll(menuTarget,targetMenuItem);

        menuBar.getMenus().addAll(menuFile, menuEdit);
        border.setTop(menuBar);

        grid.add(new Text("Last Update:"), 1, 8);
        updateTime = new Text("");
        grid.add(updateTime, 2, 8, 3, 1);

        int column = 1;
        int row = 3;
        for(DisplayBlock control : dBs.getAll())
        {
            grid.add(control, control.column, control.row);
        }

        Text scenetitle = new Text("Data");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 1, 1);

        Text tempsTitle = new Text("Temps");
        tempsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(tempsTitle, 1, 2, 1, 1);

        Text rangesTitle = new Text("Ranges");
        rangesTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(rangesTitle, 2, 2, 1, 1);

        Text switchesTitle = new Text("Switches");
        switchesTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(switchesTitle, 3, 2, 1, 1);

        primaryStage.setScene(scene);
    }

    // The createMenuItem method
    private RadioMenuItem createMenuItem (String title, String choice, ToggleGroup tg,  boolean selected)
    {
        RadioMenuItem rmi = new RadioMenuItem(title);
        rmi.setToggleGroup(tg);
        rmi.setSelected(selected);
        rmi.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val)
            {
                if(new_val == true)
                {
                    prefs.setSourceSelect(choice);
                    prefs.savePrefs();
                    dataURL = prefs.getSourceRef();
                }
            }
        });
        return rmi;
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

    private void updateUI(RemoteData rd)
    {
        DisplayBlock db;

        Set<String> keys = rd.temps.keySet();
        for (String name: keys)
        {
            db = dBs.getDisplayBlock(name);

            if(db != null)
            {
                db.setText(Double.toString(rd.temps.get(name)));
            }
            else
            {
                System.out.println("unused temp data: " + name );
            }
        }

        keys = rd.ranges.keySet();
        for (String name: keys)
        {
            db = dBs.getDisplayBlock(name);

            if(db != null)
            {
                db.setText(Double.toString(rd.ranges.get(name)));
            }
            else
            {
                System.out.println("unused range data: " + name );
            }
        }

        keys = rd.switches.keySet();
        for (String name: keys)
        {
            db = dBs.getDisplayBlock(name);

            if(db != null)
            {
                db.setText(Boolean.toString(rd.switches.get(name)));
            }
            else
            {
                System.out.println("unused switch data: " + name );
            }
        }

        updateTime.setText(new Date().toString());
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

    private static class RemoteData
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
}
