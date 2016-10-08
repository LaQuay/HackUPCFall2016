package dev.roviloapps.hackupcfall2016.model;

import java.util.ArrayList;
import java.util.Date;

public class Flight {
    private ArrayList<String> carriers;
    private Airport origin;
    private Airport destination;
    private Date date;
    private int weatherConditionDestination;
    private double temperatureScaleDestination;
    private double temperatureDestination;

    public Flight(ArrayList<String> carriers, Airport origin, Airport destination, Date date) {
        this.carriers = carriers;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
    }

    public ArrayList<String> getCarriers() {
        return carriers;
    }

    public void setCarriers(ArrayList<String> carriers) {
        this.carriers = carriers;
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWeatherConditionDestination() {
        return weatherConditionDestination;
    }

    public void setWeatherConditionDestination(int weatherConditionDestination) {
        this.weatherConditionDestination = weatherConditionDestination;
    }

    public double getTemperatureScaleDestination() {
        return temperatureScaleDestination;
    }

    public void setTemperatureScaleDestination(double temperatureScaleDestination) {
        this.temperatureScaleDestination = temperatureScaleDestination;
    }

    public double getTemperatureDestination() {
        return temperatureDestination;
    }

    public void setTemperatureDestination(double temperatureDestination) {
        this.temperatureDestination = temperatureDestination;
    }
}
