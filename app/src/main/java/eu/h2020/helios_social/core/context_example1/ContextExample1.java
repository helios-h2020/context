package eu.h2020.helios_social.core.context_example1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import eu.h2020.helios_social.core.context.ContextListener;
import eu.h2020.helios_social.core.context.ext.LocationContext;
import eu.h2020.helios_social.core.contextualegonetwork.Context;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Node;
import eu.h2020.helios_social.core.profile.HeliosUserData;
import eu.h2020.helios_social.core.sensor.SensorValueListener;
import eu.h2020.helios_social.core.sensor.ext.LocationSensor;

/**
 *  A location-based context example test application.
 *  This example creates two location-based contexts named "At home" and "At work".
 *  The location contexts (instances of the class LocationContext) take the coordinates (lat, lon) and
 *  radius (in meters) as input, which values define the area (circle) where the context is active.
 *  Further, the example shows how to receive updates to the contexts active value.
 *
 *  For example context type and sensor type implementations:
 *  @see eu.h2020.helios_social.core.context.ext.LocationContext
 *  @see eu.h2020.helios_social.core.sensor.ext.LocationSensor
 */
public class ContextExample1 extends AppCompatActivity implements ContextListener, SensorValueListener {

    private static final String TAG = ContextExample1.class.getSimpleName();

    // Code used in requesting runtime permissions
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // Constant used in the location settings dialog
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Represents a geographical location
    private Location mCurrentLocation;

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLastUpdateTimeTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mContext1ActiveView;
    private TextView mContext2ActiveView;

    // Labels
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;
    private String mContext1ActiveLabel;
    private String mContext2ActiveLabel;

    // Tracks the status of the location updates request
    private Boolean mRequestingLocationUpdates;
    // Access the location sensor
    private LocationSensor mLocationSensor;
    // example contexts
    private LocationContext locationContext1;
    private LocationContext locationContext2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_context_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);
        mLatitudeTextView = findViewById(R.id.latitude_text);
        mLongitudeTextView = findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = findViewById(R.id.last_update_time_text);
        mContext1ActiveView = findViewById(R.id.context1_active_text);
        mContext2ActiveView = findViewById(R.id.context2_active_text);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);
        mContext1ActiveLabel = getResources().getString(R.string.context1_active_label);
        mContext2ActiveLabel = getResources().getString(R.string.context2_active_label);

        mRequestingLocationUpdates = false;

        // some tests: create user profile including the home and work location info
        HeliosUserData profile = HeliosUserData.getInstance();
        profile.setValue("home_lat", "60.1805");
        profile.setValue("home_lon", "24.8255");
        profile.setValue("work_lat", "43.7208");
        profile.setValue("work_lon", "10.4083");

        // tests: retrieve home and work coordinates from the user profile
        double home_lat = Double.parseDouble(profile.getValue("home_lat"));
        double home_lon = Double.parseDouble(profile.getValue("home_lon"));
        double work_lat = Double.parseDouble(profile.getValue("work_lat"));
        double work_lon = Double.parseDouble(profile.getValue("work_lon"));

        // An example of creation of CEN using CENlibrary for user1
        // profile object is given as data for the ego node
        ContextualEgoNetwork egoNetwork = ContextualEgoNetwork.createOrLoad(getFilesDir().getPath(), "user1", profile);

        // some tests with egoNode
        Node egoNode = egoNetwork.getEgo();
        Object nodeData = egoNode.getData();
        if(nodeData != profile) {
            Log.i("Context", "User data related to egoNode not found?");
        }

        // Create location-based context named "At home".
        // The context is an instance of the LocationContext class
        locationContext1 = new LocationContext("At home", home_lat, home_lon, 1000.0);
        // Register listener to obtain changes in the context active value
        locationContext1.registerContextListener(this);
        // Create an other context "At work"
        locationContext2 = new LocationContext("At work", work_lat, work_lon, 3000.0);
        locationContext2.registerContextListener(this);

        // Init LocationSensor
        mLocationSensor = new LocationSensor(this);
        // Register location listeners for the contexts
        mLocationSensor.registerValueListener(locationContext1);
        mLocationSensor.registerValueListener(locationContext2);
        // Only for demo UI to obtain updates to location coordinates via ValueListener
        mLocationSensor.registerValueListener(this);

        /* Associate the created contexts into CEN  */
        egoNetwork.getOrCreateContext(locationContext1);
        egoNetwork.getOrCreateContext(locationContext2);

        /* Check if the contexts can be found from the CEN  */
        ArrayList<Context> cenContexts = egoNetwork.getContexts();
        for (Context c : cenContexts) {
            if (c.getData() == locationContext1) {
                Log.i("Context", "Context: At work");
            } else if(c.getData() == locationContext2) {
                Log.i("Context", "Context: At home");
            }
        }
    }

    /**
     * Implements the ContextLister interface contextChanged method, which called when context active value changed.
     * @param active - a boolean value
     */
    @Override
    public void contextChanged(boolean active) {
        Log.i("Context", "Context changed " + active);
        updateUI();
    }

    /**
     * This method implements the SensorValueListener interface receiveValue method, which
     * obtains values from the location sensor.
     * @param location - a Location value
     */
    @Override
    public void receiveValue(Object location) {
        // updates the current location
        mCurrentLocation = (Location)location;
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }

    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            mLocationSensor.startUpdates();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mLocationSensor.stopUpdates();
        mRequestingLocationUpdates = false;
        setButtonsEnabledState();
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        setButtonsEnabledState();
        updateContextUI();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingLocationUpdates);
        mStopUpdatesButton.setEnabled(mRequestingLocationUpdates);
    }

    /**
     * Sets values for the UI fields
     */
    private void updateContextUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLatitudeLabel,
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLongitudeLabel,
                    mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                    mLastUpdateTimeLabel, DateFormat.getTimeInstance().format(new Date())));
            mContext1ActiveView.setText(String.format(Locale.ENGLISH, "%s: %b", mContext1ActiveLabel,
                    locationContext1.isActive()));
            mContext2ActiveView.setText(String.format(Locale.ENGLISH, "%s: %b", mContext2ActiveLabel,
                    locationContext2.isActive()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()) {
            mLocationSensor.startUpdates();
            // mActivitySensor.startUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates
        mLocationSensor.stopUpdates();
        // mActivitySensor.stopUpdates();
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request permissions to access location
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ContextExample1.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(ContextExample1.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    mLocationSensor.startUpdates();
                }
            } else {
                // Permission denied.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
