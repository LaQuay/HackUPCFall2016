package dev.roviloapps.hackupcfall2016.model;

import java.util.Date;

public class Forecast {
    private Date date;
    private double temperature;
    private double temperatureMin;
    private double temperatureMax;

    private int temperatureScale;
    private static int TEMP_HIGH = 0;
    private static int TEMP_MEDIUM = 1;
    private static int TEMP_LOW = 2;

    public Forecast() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public int getTemperatureScale() {
        return temperatureScale;
    }

    public void setTemperatureScale() {
        int scale = TEMP_MEDIUM;
        if (temperature > 27) scale = TEMP_HIGH;
        else if (temperature < 10) scale = TEMP_LOW;

        this.temperatureScale = scale;
    }
}
