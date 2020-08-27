package eu.h2020.helios_social.core.sensor;

/**
 * SensorValueListener interface obtain values from sensor.
 *
 * This interface includes the method receiveValue, which is called when a new value is received
 * from a sensor.
 * In order to register a sensor value listener for a sensor, see the method <br/>
 * {@link eu.h2020.helios_social.core.sensor.Sensor#registerValueListener}.
 */
public interface SensorValueListener {
	/**
	 * Receives value from sensor
	 * @param value the received value
	 */
	void receiveValue(Object value);

}
