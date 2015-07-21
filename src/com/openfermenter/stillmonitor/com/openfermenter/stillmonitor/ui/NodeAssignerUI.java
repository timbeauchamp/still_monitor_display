package com.openfermenter.stillmonitor.com.openfermenter.stillmonitor.ui;

import com.openfermenter.stillmonitor.Main;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by tbeauch on 7/20/15.
 */
public class NodeAssignerUI extends BorderPane
{
    private Main main;

    public NodeAssignerUI(Stage primaryStage, Main main)
    {
        this.main = main;
        buildUI(primaryStage);
    }

    private void buildUI(Stage primaryStage)
    {
        primaryStage.setTitle("Still Monitor Display");

        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
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

        Text label = new Text();
        for(int row = 0; row < 8; row++)
        {
            for(int column = 0; column < 8; column++)
            {
                label = new Text("r:" + row + " c:" + column);
                label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                grid.add(label, column, row);
            }
        }

    }
}