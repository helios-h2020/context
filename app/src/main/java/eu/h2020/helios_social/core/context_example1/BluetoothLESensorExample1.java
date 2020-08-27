package eu.h2020.helios_social.core.context_example1;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import eu.h2020.helios_social.core.sensor.SensorValueListener;
import eu.h2020.helios_social.core.sensor.ext.BluetoothLESensor;
import eu.h2020.helios_social.core.sensor.ext.BluetoothSensor;

/**
 *  A bluetooth LE sensor scan example test application.
 *
 *  For example context type and sensor type implementations:
 *  @see BluetoothLESensor
 *  @see BluetoothSensor
 */
public class BluetoothLESensorExample1 extends AppCompatActivity  {

    private static final String TAG = BluetoothLESensorExample1.class.getSimpleName();

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private RecyclerView mBluetoothView;

    // Tracks the status of the Bluetooth updates request
    private Boolean mRequestingBTUpdates;
    // Access the Bluetooth sensor
    private BluetoothLESensor mBluetoothSensor;

    private LinearLayoutManager layoutManager;

    private myBTAdapter mAdapter;

    private LinkedHashMap<String, BTDeviceItem> mBtDevices;

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_context_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);
        mBluetoothView = findViewById(R.id.bluetooth_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mBluetoothView.setLayoutManager(layoutManager);

        // hashmap of found BT devices
        mBtDevices = new LinkedHashMap<>();
        // specify an adapter
        mAdapter = new myBTAdapter(mBtDevices);
        mBluetoothView.setAdapter(mAdapter);

        mRequestingBTUpdates = false;

        // New Bluetooth LE Sensor
        mBluetoothSensor = new BluetoothLESensor(this);
        // Create sensor value listener, which obtains notifications about Bluetooth devices in range
        SensorValueListener BTListener = new SensorValueListener() {
            @Override
            public void receiveValue(Object value) {
                Log.d(TAG, "Received value");
                Object[] bt = (Object[])value;
                BluetoothDevice device = (BluetoothDevice) bt[0];
                int rssi = (Integer) bt[1];
                String hwaddr = device.getAddress();
                String name = device.getName();
                String msg = "Received new bt device, id:" + hwaddr + ", rssi:" + rssi + ", name:" + name;
                Log.d(TAG, msg);
                mBtDevices.put(hwaddr, new BTDeviceItem(name, hwaddr, rssi));
                mAdapter.notifyDataSetChanged();
            }
        };
        mBluetoothSensor.registerValueListener(BTListener);
    }

    public class myBTAdapter extends RecyclerView.Adapter<myBTAdapter.MyViewHolder> {
        private LinkedHashMap<String, BTDeviceItem> dataset;

        // Reference to the views for each data item
        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView btAddressView;
            private TextView btNameView;
            private TextView btUpdateTimeView;
            private TextView btRssiView;
            public MyViewHolder(View itemView) {
                super(itemView);
                this.btAddressView = itemView.findViewById(R.id.btDeviceAddrView);
                this.btNameView = itemView.findViewById(R.id.btDeviceNameView);
                this.btUpdateTimeView = itemView.findViewById(R.id.btDeviceUpdateTimeView);
                this.btRssiView = itemView.findViewById(R.id.btRssiView);
            }
        }

        public myBTAdapter(LinkedHashMap<String, BTDeviceItem> myDataset) {
            dataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        public myBTAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_bluetooth_device, parent, false);
            myBTAdapter.MyViewHolder viewHolder = new myBTAdapter.MyViewHolder(listItem);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            BTDeviceItem bt = (BTDeviceItem)dataset.values().toArray()[position];
            holder.btAddressView.setText(bt.getBtHwAddress());
            holder.btNameView.setText(bt.getBtDeviceName());
            holder.btUpdateTimeView.setText(bt.getUpdateTime());
            holder.btRssiView.setText("RSSI: "+ bt.getRssi());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

    public class BTDeviceItem {
        final String btDeviceName;
        final String btHwAddress;
        final String updateTime;
        final int rssi;

        public BTDeviceItem(String btDeviceName, String btHwAddress, int rssi) {
            this.btDeviceName = btDeviceName;
            this.btHwAddress = btHwAddress;
            this.rssi = rssi;
            this.updateTime = dateFormat.format(new Date());
        }

        public String getBtDeviceName() {
            return btDeviceName;
        }

        public String getBtHwAddress() {
            return btHwAddress;
        }

        public int getRssi() {
            return rssi;
        }

        public String getUpdateTime() { return updateTime; }
    }

    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingBTUpdates) {
            mRequestingBTUpdates = true;
            mBluetoothSensor.startUpdates();
            setButtonsEnabledState();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mRequestingBTUpdates = false;
        mBluetoothSensor.stopUpdates();
        setButtonsEnabledState();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingBTUpdates);
        mStopUpdatesButton.setEnabled(mRequestingBTUpdates);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingBTUpdates) {
            mBluetoothSensor.startUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestingBTUpdates) {
            mBluetoothSensor.stopUpdates();
        }
    }

}
