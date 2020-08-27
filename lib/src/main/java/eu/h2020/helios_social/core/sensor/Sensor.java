package eu.h2020.helios_social.core.sensor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sensor is an abstract base class for Helios sensors (e.g. location and acceleration sensors).
 * An application can obtain updated values from the sensor via registered listeners (see registerValueListener method),
 * which implement the SensorValueListener interface.
 * @see eu.h2020.helios_social.core.sensor.SensorValueListener
 */
public abstract class Sensor {

	private final List<SensorValueListener> sensorListeners;

	/**
	 * Creates a Sensor
	 */
	public Sensor() {
		this.sensorListeners = new ArrayList<SensorValueListener>();
	}

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
		Iterator itr = sensorListeners.iterator();
		while (itr.hasNext()) {
			((SensorValueListener) itr.next()).receiveValue(value);
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
}
