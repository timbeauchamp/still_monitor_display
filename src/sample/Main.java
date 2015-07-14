package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application
{

    // public static String dataURL = "http://localhost://temps.xml";
    public static String dataURL = "http://192.168.1.15/temps.xml";
    Text updateTime;
    RemoteData rd = new RemoteData();
    Timer timer;

    Label[] tempControl   = new Label[4];
    Label[] rangeControl  = new Label[4];
    Label[] switchControl = new Label[4];

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
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

        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run()
                    {
                        System.out.println("Updating UI");
                        updateUI(doc2RemoteData(getData(dataURL)));
                    }
                });
            }
        }, 5000, 10000);
    }


    private void buildUI(Stage primaryStage)
    {
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Still Monitor Display");

        tempControl[0] = new Label("Temp 1");
        tempControl[1] = new Label("Temp 2");
        tempControl[2] = new Label("Temp 3");
        tempControl[3] = new Label("Temp 4");

        rangeControl[0] = new Label("Range 1");
        rangeControl[1] = new Label("Range 2");
        rangeControl[2] = new Label("Range 3");
        rangeControl[3] = new Label("Range 4");

        switchControl[0] = new Label("Switch 1");
        switchControl[1] = new Label("Switch 2");
        switchControl[2] = new Label("Switch 3");
        switchControl[3] = new Label("Switch 4");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(new Text("Last Update:"), 1, 8);
        updateTime = new Text("");
        grid.add(updateTime, 2, 8, 3, 1);

        grid.add(tempControl[0], 1, 3);
        grid.add(tempControl[1], 1, 4);
        grid.add(tempControl[2], 1, 5);
        grid.add(tempControl[3], 1, 6);

        grid.add(rangeControl[0], 2, 3);
        grid.add(rangeControl[1], 2, 4);
        grid.add(rangeControl[2], 2, 5);
        grid.add(rangeControl[3], 2, 6);

        grid.add(switchControl[0], 3, 3);
        grid.add(switchControl[1], 3, 4);
        grid.add(switchControl[2], 3, 5);
        grid.add(switchControl[3], 3, 6);

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

        Scene scene = new Scene(grid, 600, 475);
        primaryStage.setScene(scene);
    }

    private static Document getData(String urlStr)
    {
        URL url;
        Document doc = null;
        try
        {
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

        if (len > 0)
            rd.temps = new double[len];

        Node currentNode;
        String nodeVal;
        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.temps[i] = Double.parseDouble(nodeVal);
        }

        currentNodeList = doc.getElementsByTagName("range");
        len = currentNodeList.getLength();

        if (len > 0)
            rd.ranges = new double[len];

        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.ranges[i] = Double.parseDouble(nodeVal);
        }

        currentNodeList = doc.getElementsByTagName("switch");
        len = currentNodeList.getLength();

        if (len > 0)
            rd.switches = new boolean[len];

        for (int i = 0; i < len; i++)
        {
            currentNode = currentNodeList.item(i);
            nodeVal = currentNode.getFirstChild().getNodeValue();
            rd.switches[i] = Boolean.parseBoolean(nodeVal);
        }
        return rd;
     }

    private void updateUI(RemoteData rd)
    {
        int index = 0;
        for (double temp : rd.temps)
        {
            if (index > 3)
                break;
            tempControl[index].setText(Double.toString(rd.temps[index++]));
        }

        index = 0;
        for (double range : rd.ranges)
        {
            if (index > 3)
                break;
            rangeControl[index].setText(Double.toString(rd.ranges[index++]));
        }

        index = 0;
        for (boolean aSwitch : rd.switches)
        {
            if (index > 3)
                break;
            switchControl[index].setText(Boolean.toString(rd.switches[index++]));
        }
        updateTime.setText(new Date().toString());
        System.out.println("Updated UI");
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
        public double[] temps = new double[0];
        public double[] ranges = new double[0];
        public boolean[] switches = new boolean[0];

        @Override
        public String toString()
        {
            return "Num Temps:"+ temps.length + "  Num Ranges:"+ ranges.length + "  Num Swithces: "+ switches.length;
        }
    }
}
