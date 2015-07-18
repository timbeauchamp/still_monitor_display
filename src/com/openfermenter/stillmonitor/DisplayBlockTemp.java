package com.openfermenter.stillmonitor;

/**
 * Created by tbeauch on 7/18/15.
 */
public class DisplayBlockTemp extends DisplayBlock
{
    private  static final int min = -10;
    private  static final int max = 110;

    public DisplayBlockTemp(String id, int column, int row)
    {
        super(id, column, row);
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);
        String colorString = "";
        double temp = Double.parseDouble(text);
        double tempC = temp;
        double tempF = tempC * 1.8 + 32.0;
        if(temp > 96.0)
        {
//            colorString = getRGB(min, max, temp);
            colorString = "ff0000";
        }
        else if(temp > 76.0)
        {
            colorString = "ffff00";
        }
        else if(temp > 65.0)
        {
            colorString = "00ff00";
        }
        else
        {
            colorString = "00ffff";
        }

        label.setStyle("-fx-background-color: #" + colorString + ";");
        label.setText(String.format("%1$,.1f C (%2$,.1f F)",tempC, tempF));

    }

    public static String getRGB(double minimum, double maximum, double value)
    {
        StringBuilder rgbStrBldr = new StringBuilder();
        double ratio = 2 * (value-minimum) / (maximum - minimum);
        int b = (int)(Math.max(0, 255 * (1 - ratio)));
        int r = (int)(Math.max(0, 255 * (ratio - 1)));
        int g = 255 - b - r;

        rgbStrBldr.append(Integer.toHexString(0x100 | r).substring(1).toUpperCase());
        rgbStrBldr.append(Integer.toHexString(0x100 | b).substring(1).toUpperCase());
        rgbStrBldr.append(Integer.toHexString(0x100 | b).substring(1).toUpperCase());


//        0x10000 | i).substring(1).toUpperCase());
        return rgbStrBldr.toString();
    }
}
