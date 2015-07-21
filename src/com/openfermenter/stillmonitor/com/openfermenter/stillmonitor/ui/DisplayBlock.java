package com.openfermenter.stillmonitor.com.openfermenter.stillmonitor.ui;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by tbeauch on 7/16/15.
 */
public class DisplayBlock extends Pane
{
    private String id = "";
    private String text = "";
    protected Label label;
    private int row = 0;
    private int column = 0;

    public DisplayBlock()
    {
        super();
        label = new Label();
        this.getChildren().add(label);
    }

    public DisplayBlock(String id, int column, int row)
    {
        this();
        this.id = id;
        this.setRow(row);
        this.setColumn(column);
        label.setText(text);
    }

    public void setText(String text)
    {
        this.text = text;
        label.setText(text);
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public String getText()
    {
        return this.text;
    }

    public String getID()
    {
        return this.id;
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn(int column)
    {
        this.column = column;
    }

    public int getRow()
    {
        return row;
    }

    public void setRow(int row)
    {
        this.row = row;
    }
}
