package com.openfermenter.stillmonitor.com.openfermenter.stillmonitor.ui;

import com.openfermenter.stillmonitor.Main;
import com.openfermenter.stillmonitor.RemoteData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.util.Date;
import java.util.Set;

/**
 * Created by tbeauch on 7/20/15.
 */
public class MainUI extends BorderPane
{
    private Text updateTime;
    private Main main;
    private Scene myScene;

    public MainUI(Stage primaryStage, Main main)
    {
        this.main = main;
        buildUI(primaryStage);
    }

    private void buildUI(Stage primaryStage)
    {
        primaryStage.setTitle("Still Monitor Display");

        GridPane grid = new GridPane();
//        grid.setGridLinesVisible(true);
//        grid.setStyle("-fx-background-color: #336699;");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
//        grid.setPadding(new Insets(250, 250, 250, 250));

//        ColumnConstraints columnConstraints = new ColumnConstraints();
//        columnConstraints.setFillWidth(true);
//        columnConstraints.setHgrow(Priority.ALWAYS);
//        grid.getColumnConstraints().add(columnConstraints);
//
//        RowConstraints rowConstraints = new RowConstraints();
//        rowConstraints.setFillHeight(true);
//        rowConstraints.setVgrow(Priority.ALWAYS);
//        grid.getRowConstraints().add(rowConstraints);

  //      BorderPane border = new BorderPane();
        setCenter(grid);
        Scene scene = new Scene(this, 600, 475);


        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuTarget = new Menu("Target");

        String targetString = Main.prefs.getSourceSelect();
        ToggleGroup tg = new ToggleGroup();
        // --- Creating check menu items
        RadioMenuItem localhostTarget = createMenuItem ("Localhost", "localhost", tg, targetString.equalsIgnoreCase("localhost"));
        RadioMenuItem localfileTarget = createMenuItem ("Local File", "localfile", tg, targetString.equalsIgnoreCase("localfile"));
        RadioMenuItem raspberryTarget = createMenuItem ("Raspberry", "raspberry", tg, targetString.equalsIgnoreCase("raspberry"));
        menuTarget.getItems().addAll(localhostTarget, localfileTarget, raspberryTarget);



        MenuItem assignerMenuItem = new MenuItem("Change UI");
        assignerMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t)
            {
                main.showAssigner();
            }
        });

        MenuItem showMainMenuItem = new MenuItem("Show Main Screen");
        showMainMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t)
            {
                main.showMainUI();
            }
        });

        menuEdit.getItems().addAll(menuTarget,assignerMenuItem, showMainMenuItem);

        menuBar.getMenus().addAll(menuFile, menuEdit);
        setTop(menuBar);

        grid.add(new Text("Last Update:"), 1, 8);
        updateTime = new Text("");
        grid.add(updateTime, 2, 8, 3, 1);

        for(DisplayBlock control : Main.dBs.getAll())
        {
            grid.add(control, control.getColumn(), control.getRow());
        }

        Text tempsTitle = new Text("Temps");
        tempsTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(tempsTitle, 1, 2, 1, 1);

        Text rangesTitle = new Text("Ranges");
        rangesTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(rangesTitle, 2, 2, 1, 1);

        Text switchesTitle = new Text("Switches");
        switchesTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(switchesTitle, 3, 2, 1, 1);

//        primaryStage.setScene(scene);
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
                    Main.prefs.setSourceSelect(choice);
                    Main.prefs.savePrefs();
                    Main.dataURL = Main.prefs.getSourceRef();
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

    public void updateUI(RemoteData rd)
    {
        DisplayBlock db;

        Set<String> keys = rd.temps.keySet();
        for (String name: keys)
        {
            db = Main.dBs.getDisplayBlock(name);

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
            db = Main.dBs.getDisplayBlock(name);

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
            db = Main.dBs.getDisplayBlock(name);

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

//    public Scene getScene();
//    {
//        return myScene;
//    }
}
