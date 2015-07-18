package com.openfermenter.stillmonitor;

import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Created by tbeauch on 7/16/15.
 */
public class DisplayBlock extends Parent
{
    private String id = "";
    private String text = "";
    Label label;
    int row = 0;
    int column = 0;

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
        this.row = row;
        this.column = column;
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
}
