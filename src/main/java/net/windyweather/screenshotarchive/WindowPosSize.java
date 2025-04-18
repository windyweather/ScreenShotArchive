package net.windyweather.screenshotarchive;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/*
      Class for the Window Data
   */
public class WindowPosSize {


    public SimpleDoubleProperty dPosX;
    public SimpleDoubleProperty dPosY;
    public SimpleDoubleProperty dSizeX;
    public SimpleDoubleProperty dSizeY;


    public WindowPosSize() {
    }

    void setStuffToZero() {
        if (true) {
            dPosX = new SimpleDoubleProperty(0);
            dPosY = new SimpleDoubleProperty(0);
            dSizeX = new SimpleDoubleProperty(0);
            dSizeY = new SimpleDoubleProperty(0);
        }
    }


    public double getDPosX( ) {
        return dPosX.get();
    }

    public double getDPosY( ) {
        return dPosY.get();
    }

    public double getDSizeX( ) {
        return dSizeX.get();
    }

    public double getDSizeY( ) {
        return dSizeY.get();
    }

    public void setDPosX( double x ) {
        this.dPosX = new SimpleDoubleProperty(x);
    }

    public void setDPosY( double y ) {
        this.dPosY = new SimpleDoubleProperty(y);
    }

    public void setDSizeX( double width ) {
        this.dSizeX = new SimpleDoubleProperty(width);
    }

    public void setDSizeY( double height ) {
        this.dSizeY = new SimpleDoubleProperty(height);
    }

}
