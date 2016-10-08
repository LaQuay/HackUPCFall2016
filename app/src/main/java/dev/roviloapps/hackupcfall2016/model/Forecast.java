package dev.roviloapps.hackupcfall2016.model;

import java.util.Date;

public class Forecast {
    public static int TEMP_HIGH = 0;
    public static int TEMP_MEDIUM = 1;
    public static int TEMP_LOW = 2;
    public static int WEATHER_CLOUDS = 0;
    public static int WEATHER_RAINY = 1;
    public static int WEATHER_CLEAR = 2;
    private Date date;
    private double temperature;
    private double temperatureMin;
    private double temperatureMax;
    private int temperatureScale;
    private int weatherCondition;

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
        int scale = TEMP_LOW;
        if (temperature > 24) scale = TEMP_HIGH;

        this.temperatureScale = scale;
    }

    public int getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weather) {
        weatherCondition = WEATHER_CLEAR;
        if (weather.equals("Rain")) weatherCondition = WEATHER_RAINY;
        else if (weather.equals("Clouds")) weatherCondition = WEATHER_CLOUDS;
    }
}
