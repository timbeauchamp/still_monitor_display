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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application
{

    public static String dataURL = "http://localhost://temps.xml"; // http://192.168.1.15/temps.xml;
    Text resultText;
    RemoteData rd = new RemoteData();

    Label[] tempControl = new Label[3];
    Label[] rangeControl = new Label[3];
    Label[] switchControl = new Label[3];

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

    public void startUpdater()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                // Update the text node with calculated results
            }
        });
    }


    private void buildUI(Stage primaryStage)
    {
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Still Monitor Display");

        Button btn = new Button();
        btn.setText("Update Temps");
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                DataRetrievalService service = new DataRetrievalService();
                service.setUrl(dataURL);
                service.setOnSucceeded(new EventHandler<WorkerStateEvent>()
                {
                    @Override
                    public void handle(WorkerStateEvent t)
                    {
                        RemoteData rd = (RemoteData) t.getSource().getValue();
//                        System.out.println("done:" + rd.toString());
                        updateUI(rd);
                    }
                });
                service.start();
            }
        });

        tempControl[0] = new Label("Temp 1");
        tempControl[1] = new Label("Temp 2");
        tempControl[2] = new Label("Temp 3");

        rangeControl[0] = new Label("Range 1");
        rangeControl[1] = new Label("Range 2");
        rangeControl[2] = new Label("Range 3");

        switchControl[0] = new Label("Switch 1");
        switchControl[1] = new Label("Switch 2");
        switchControl[2] = new Label("Switch 3");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        final Text actiontarget = new Text("");
        grid.add(actiontarget, 2, 6);

        grid.add(tempControl[0], 1, 3);
        grid.add(tempControl[1], 1, 4);
        grid.add(tempControl[2], 1, 5);

        grid.add(rangeControl[0], 2, 3);
        grid.add(rangeControl[1], 2, 4);
        grid.add(rangeControl[2], 2, 5);

        grid.add(switchControl[0], 3, 3);
        grid.add(switchControl[1], 3, 4);
        grid.add(switchControl[2], 3, 5);

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

        Text resultTitle = new Text("results:");
        resultTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(resultTitle, 0, 7, 3, 1);

        resultText = new Text("data dasfasf dasf a fas f f asf a fas f a");
        resultText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(resultText, 1, 7, 3, 1);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 6);

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
            if (index > 2)
                break;
            tempControl[index].setText(Double.toString(rd.temps[index++]));
        }

        index = 0;
        for (double range : rd.ranges)
        {
            if (index > 2)
                break;
            rangeControl[index].setText(Double.toString(rd.ranges[index++]));
        }

        index = 0;
        for (boolean aSwitch : rd.switches)
        {
            if (index > 2)
                break;
            switchControl[index].setText(Boolean.toString(rd.switches[index++]));
        }
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
