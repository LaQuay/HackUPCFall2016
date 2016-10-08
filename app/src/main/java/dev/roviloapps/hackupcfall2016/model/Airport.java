package dev.roviloapps.hackupcfall2016.model;

/**
 * Created by LaQuay on 08/10/2016.
 */

public class Airport {
    private String code;
    private double latitude;
    private double longitude;
    private String name;
    private String city;
    private String country;

    public Airport(String code, double latitude, double longitude, String name, String city, String country) {
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return code + " - " + name + ", " + city + ", " + country;
    }
}
