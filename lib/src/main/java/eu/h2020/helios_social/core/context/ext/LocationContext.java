package eu.h2020.helios_social.core.context.ext;

import android.location.Location;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.sensor.SensorValueListener;

/**
 *  LocationContext - a location-based context class.<br/>
 *  This class extends the base class Context, and it
 *  is defined by location center coordinates and radius.
 *  Location value updates are obtained from LocationSensor via SensorValueListener
 */
public class LocationContext extends Context implements SensorValueListener {

    double lat, lon;
    double radius;
    static final int LOCATION_CONTEXT_TYPE=1;
    Location location; // current location

    /**
     * Creates a LocationContext.
     * The context is defined by the latitude (lat) and longitude (lon) values and the radius,
     * and is active in the circular area defined by center coordinates (lat, lon) and radius.
     * @param name the name of the context
     * @param lat the latitude value of the center point
     * @param lon the longitude value of the center point
     * @param radius the radius of the circle
     */
    public LocationContext(String name, double lat, double lon, double radius) {
        super(LOCATION_CONTEXT_TYPE, name, false);
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
        this.location = null;
    }

    /**
     * Gets the latitude value of this context
     * @return the latitude value
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the latitude value (lat) of this context
     * @param lat the latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Gets the longitude value (lon) of this context
     * @return the longitude value
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the longitude value (lon) of this context
     * @param lon the longitude
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Gets the radius of this context
     * @return the radius value
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius value
     * @param radius the radius value
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Updates the current location value
     * @param location the updated location
     */
    public void updateLocation(Location location) {
        if(location != null) {
            this.location = location;
            double lat1 = location.getLatitude();
            double lon1 = location.getLongitude();
            if(distanceLatLon2(lat, lon, lat1, lon1) < radius) {
                // Log.i("LocationContext","updateLocation" + lat1 + ", " + lon1 + getName() + ",true" + getLat() + ',' + getLon());
                setActive(true);
            } else {
                // Log.i("LocationContext","updateLocation" + lat1 + ", " + lon1 + getName() + ",false" + getLat() + ',' + getLon());
                setActive(false);
            }
        }
    }

    /**
     * Gets the current location value
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Calculates approximate distance between two points
     */
    private static double distanceLatLon2(double lat1, double lon1, double lat2, double lon2) {
        double sc = 111120.0; // == 60 * 1852;
        double xd = (lat1 - lat2) * sc;
        double yd = (lon1 - lon2) * sc * Math.cos(Math.toRadians(lat2));
        return Math.sqrt(xd * xd + yd * yd);
    }

    /**
     * Receives location value from LocationSensor.
     * This method implements the SensorValueListener interface
     * method, which is called when the related sensor obtains updated location value.
     * @param value the received Location value
     */
    @Override
    public void receiveValue(Object value) {
        updateLocation((Location)value);
    }
}
