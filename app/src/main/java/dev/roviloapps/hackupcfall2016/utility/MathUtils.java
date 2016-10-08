package dev.roviloapps.hackupcfall2016.utility;

import android.location.Location;

/**
 * Created by LaQuay on 08/10/2016.
 */

public class MathUtils {
    public static float distanceBetweenTwoLocations(double latitude_0, double longitude_0, double latitude_1, double longitude_1) {
        Location location_0 = new Location("point 0");
        location_0.setLatitude(latitude_0);
        location_0.setLongitude(longitude_0);

        Location location_1 = new Location("point 1");
        location_1.setLatitude(latitude_1);
        location_1.setLongitude(longitude_1);

        return location_0.distanceTo(location_1);
    }
}
