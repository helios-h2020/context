package eu.h2020.helios_social.core.context_example1;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import eu.h2020.helios_social.core.context.ContextListener;
import eu.h2020.helios_social.core.context.ext.WifiContext;
import eu.h2020.helios_social.core.sensor.SensorValueListener;
import eu.h2020.helios_social.core.sensor.ext.WifiSensor;

/**
 *  A WIFI-based context example test application.
 *  This example creates a WifiContext named "work wifi".
 *  The example WifiContext is used to detect whether a wifi given ssid is currently connected.
 *  Further, the example shows how to receive updates to the contexts active value.
 *
 *  For example context type and sensor type implementations:
 *  @see WifiContext
 *  @see WifiSensor
 */
public class WifiContextExample1 extends AppCompatActivity implements ContextListener, SensorValueListener {

    private static final String TAG = WifiContextExample1.class.getSimpleName();

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mWifiContextView;
    private EditText mWifiNameView;
    private TextView mCurrentWifiView;
    private TextView mCurrentWifiNameView;

    // Tracks the status of the location updates request
    private Boolean mRequestingWifiUpdates;

    // Wifi context
    private WifiContext mWifiContext;
    // Access the Wifi sensor
    private WifiSensor mWifiSensor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_context_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);
        mWifiContextView = findViewById(R.id.wifiContext);
        mWifiNameView = findViewById(R.id.wifiName);
        mCurrentWifiView = findViewById(R.id.currentWifi);
        mCurrentWifiNameView = findViewById(R.id.currentWifiName);

        mRequestingWifiUpdates = false;

        mWifiNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ssid = charSequence.toString();
                mWifiContext.setSsid(ssid);
                Log.d(TAG, "WifiContext ssid changed:" + ssid);
                mWifiContext.receiveValue(mWifiSensor.getWifiSSID()); // to update the view
            }
            @Override
            public void afterTextChanged(Editable ssid) {}
        });

        // Create new Wifi context "work wifi"
        mWifiContext = new WifiContext("work wifi", "HELIOS WIFI");
        mWifiSensor = new WifiSensor(this);

        mWifiSensor.registerValueListener(mWifiContext);
        mWifiSensor.registerValueListener(this);
        mWifiContext.registerContextListener(this);

        mWifiNameView.setText(mWifiContext.getSsid());
    }

    /**
     * Implements the ContextLister interface contextChanged method, which called when context active value changed.
     * @param active - a boolean value
     */
    @Override
    public void contextChanged(boolean active) {
        Log.i(TAG, "Context changed " + active);
        mWifiContextView.setBackgroundColor(active ? Color.GREEN : Color.rgb(221,221,221));
    }

    /**
     * This method implements the SensorValueListener interface receiveValue method, which
     * obtains values from the Wifi sensor.
     * @param value - a Wifi SSID value
     */
    @Override
    public void receiveValue(Object value) {
        // updates the current SSID
        String received_ssid = (String) value;
        Log.i(TAG, "ReceiveValue: " + received_ssid);
        mCurrentWifiNameView.setText(received_ssid);
    }

    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingWifiUpdates) {
            mRequestingWifiUpdates = true;
            setButtonsEnabledState();
            mWifiSensor.startUpdates();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mWifiSensor.stopUpdates();
        mRequestingWifiUpdates = false;
        setButtonsEnabledState();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingWifiUpdates);
        mStopUpdatesButton.setEnabled(mRequestingWifiUpdates);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingWifiUpdates) {
            mWifiSensor.startUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates
        if (mRequestingWifiUpdates) {
            mWifiSensor.stopUpdates();
        }
    }

}
