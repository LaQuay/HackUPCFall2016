package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.model.Airport;

/**
 * Created by LaQuay on 08/10/2016.
 */

public class AirportController {
    private static AirportController instance;
    private final Context ctx;
    private ArrayList<Airport> airports;

    private AirportController(Context ctx) {
        this.ctx = ctx;
    }

    public static AirportController getInstance(Context ctx) {
        if (instance == null) {
            createInstance(ctx);
        }
        return instance;
    }

    private synchronized static void createInstance(Context ctx) {
        if (instance == null) {
            instance = new AirportController(ctx);
        }
    }

    public Airport getAirport(String code) {
        for (int i = 0; i < airports.size(); ++i) {
            if (airports.get(i).getCode().equals(code)) {
                return airports.get(i);
            }
        }
        return null;
    }

    public ArrayList<Airport> getAirports() {
        return airports;
    }

    public void setAirports(ArrayList<Airport> airports) {
        this.airports = airports;
    }
}
