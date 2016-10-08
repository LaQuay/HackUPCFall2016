package dev.roviloapps.hackupcfall2016.model;

import java.util.Date;

public class Forecast {
    private Date date;
    private double temperature;
    private double temperatureMin;
    private double temperatureMax;

    public Forecast() {
    }

    public Forecast(Date date, double temperature, double temperatureMin, double temperatureMax) {
        this.date = date;
        this.temperature = temperature;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
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
}
