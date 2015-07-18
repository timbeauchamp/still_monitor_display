package com.openfermenter.stillmonitor;

import java.text.DecimalFormat;

/**
 * Created by tbeauch on 7/18/15.
 */
public class DisplayBlockRange extends DisplayBlock
{

    public DisplayBlockRange(String id, int column, int row)
    {
        super(id, column, row);
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);

        String colorString = "";
        double range = Double.parseDouble(text);
        if(range > 0.75)
        {
//            colorString = getRGB(min, max, temp);
            colorString = "ff0000";
        }
        else if(range > 0.50)
        {
            colorString = "ffff00";
        }
        else if(range > 0.25)
        {
            colorString = "00ff00";
        }
        else
        {
            colorString = "00ffff";
        }

        label.setStyle("-fx-background-color: #" + colorString + ";");

        label.setText(String.format("%1$,.2f",range));

    }
}
