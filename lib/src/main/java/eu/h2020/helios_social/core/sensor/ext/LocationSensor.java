package eu.h2020.helios_social.core.sensor.ext;

import android.content.ContextWrapper;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  This class provides an implementation of sensor API for obtaining GPS location.
 *  LocationSensor is a subclass of the abstract base class Sensor.
 *  It uses Android Fused Location Provider API to obtain position coordinates and updates to coordinates,
 *  as well as start and stop the location updates
 */
public class LocationSensor extends Sensor {

    private Location mCurrentLocation;
    // access to the Fused Location Provider API
    private final FusedLocationProviderClient mFusedLocationClient;
    // access to the Location Settings API
    private final SettingsClient mSettingsClient;

    private LocationRequest mLocationRequest;

    private LocationSettingsRequest mLocationSettingsRequest;

    private LocationCallback mLocationCallback;

    private boolean mRequestingLocationUpdates;

    private final int mPriority;
    // update intervals
    private final int mUpdateInterval;
    private final int mFastestUpdateInterval;
    static final int UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    /**
     * Creates a LocationSensor
     * @param appContext the application environment (e.g. an Activity or a Service)
     */
    public LocationSensor(ContextWrapper appContext) {
        this(appContext, UPDATE_INTERVAL_IN_MILLISECONDS, FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS, LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates LocationSensor
     * @param appContext the application environment (e.g. an Activity or a Service)
     * @param updateInterval the update interval of the location
     * @param fastestUpdateInterval the fastest update interval
     * @param priority the location reguest priority value
     */
    public LocationSensor(ContextWrapper appContext, int updateInterval, int fastestUpdateInterval, int priority) {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
        this.mSettingsClient = LocationServices.getSettingsClient(appContext);
        this.mCurrentLocation = null;
        this.mRequestingLocationUpdates = false;
        this.mUpdateInterval = updateInterval;
        this.mFastestUpdateInterval = fastestUpdateInterval;
        this.mPriority = priority;
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    /**
     * Requests location updates
     */
    @Override
    public void startUpdates() {
        if(mRequestingLocationUpdates) {
            return;
        }
        // check if the device has the necessary location settings
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        try {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                            mRequestingLocationUpdates = true;
                        } catch(SecurityException e) {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                mRequestingLocationUpdates = false;
                                break;
                        }
                    }
                });
    }

    /**
     * Stops location updates
     */
    @Override
    public void stopUpdates() {
        if (!mRequestingLocationUpdates) {
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    /**
     * Sets up the location request
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create(); // new LocationRequest();

        // set the desired interval for active location updates.
        mLocationRequest.setInterval(mUpdateInterval);

        // set the fastest rate for active location updates.
        mLocationRequest.setFastestInterval(mFastestUpdateInterval);

        // set priority
        mLocationRequest.setPriority(mPriority);
    }

    /**
     * Builds location settings request
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Creates a callback for receiving location events.
     * Notifies all the registered SensorValueListeners the updated Location value.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                if(mCurrentLocation != null) {
                    // New location obtained from the sensor. Notify the listeners by calling the listeners'
                    // receiveValue method with the new Location value as argument.
                    receiveValue(mCurrentLocation);
                }
            }
        };
    }

}
