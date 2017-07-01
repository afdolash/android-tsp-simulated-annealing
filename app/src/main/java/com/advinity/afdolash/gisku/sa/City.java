package com.advinity.afdolash.gisku.sa;

/**
 * Created by Afdolash on 7/1/2017.
 */

public class City {
    private double x;
    private double y;

    //Constructor
    //creates a city given its name and (x,y) location
    public City(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

}
