package eu.h2020.helios_social.core.sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sensor is an abstract base class for Helios sensors (e.g. location and acceleration sensors).
 * An application can obtain updated values from the sensor via registered listeners (see registerValueListener method),
 * which implement the SensorValueListener interface.
 * @see eu.h2020.helios_social.core.sensor.SensorValueListener
 */
public abstract class Sensor {

	// identifier of this
	private final String id;
	// listeners of this sensor
	private final List<SensorValueListener> sensorListeners;
	// all sensors
	private static final Map<String,Sensor> sensors = new HashMap<String, Sensor>();

	/**
	 * Creates a Sensor
	 * @param id the identifier of this sensor.
	 */
	public Sensor(String id) {
		this.id =  (id == null) ? UUID.randomUUID().toString() : id;
		this.sensorListeners = new ArrayList<SensorValueListener>();
		this.sensors.put(id, this);
	}

	/**
	 * Creates a Sensor
	 */
	public Sensor() {
		this(null);
	}

	/**
	 * Gets identifier of this sensor
	 * @return the identifier of this sensor
	 */
	public final String getId() { return id; }

	/**
	 * Starts receiving sensor data values
	 */
	public abstract void startUpdates();

	/**
	 * Stops receiving sensor data values
	 */
	public abstract void stopUpdates();

	/**
	 * Receives a data value from the sensor and notifies all
	 * the registered SensorValueListeners.
	 * @param value the value
	 */
	public void receiveValue(Object value) {
		for (SensorValueListener sensorListener : sensorListeners) {
			sensorListener.receiveValue(value);
		}
	}

	/**
	 * Registers a value listener for this sensor
	 * @param listener the listener
	 */
	public void registerValueListener(SensorValueListener listener) {
		sensorListeners.add(listener);
	}

	/**
	 * Unregister a listener
	 * @param listener the listener
	 */
	public void unregisterValueListener(SensorValueListener listener) {
		sensorListeners.remove(listener);
	}

	/**
	 * Gets all the registered listeners
	 * @return the listeners
	 */
	public Iterator<SensorValueListener> getValueListeners() {
		return sensorListeners.iterator();
	}

	/**
	 * Returns Sensor by given id
	 * @param id the identifier
	 * @return the sensor
	 */
	public static Sensor getSensorById(String id) {
		return sensors.get(id);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.sensors.remove(id, this);
	}
}
