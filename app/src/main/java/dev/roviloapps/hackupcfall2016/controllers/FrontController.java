package dev.roviloapps.hackupcfall2016.controllers;

import android.content.Context;

import java.util.ArrayList;

import dev.roviloapps.hackupcfall2016.model.Airport;

/**
 * Created by LaQuay on 08/10/2016.
 */

public class FrontController {

    private static FrontController instance;
    private final Context ctx;
    private ArrayList<Airport> airports;

    private FrontController(Context ctx) {
        this.ctx = ctx;
    }

    public static FrontController getInstance(Context ctx) {
        if (instance == null) {
            createInstance(ctx);
        }
        return instance;
    }

    private synchronized static void createInstance(Context ctx) {
        if (instance == null) {
            instance = new FrontController(ctx);
        }
    }

    public ArrayList<Airport> getAirports() {
        return airports;
    }

    public void setAirports(ArrayList<Airport> airports) {
        this.airports = airports;
    }
}
