package eu.h2020.helios_social.core.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 * ContextSource
 * - a context source may relate to different types of sensors and
 * other information sources.
 *
 */
public class ContextSource  {
	private final String sourceName;
	private final List<Sensor> sensors;

	/**
	 * Creates a context source instance
	 * @param sourceName
	 */
	public ContextSource(String sourceName) {
		this.sourceName = sourceName;
		this.sensors = new ArrayList<Sensor>();
	}

	/**
	 * Registers a sensor for this context source
	 * @param sensor
	 */
	public void registerSensor(Sensor sensor) {
		sensors.add(sensor);
	}

	/**
	 * Unregisters a sensor
	 * @param sensor
	 */
	public void unRegisterSensor(Sensor sensor) {
		sensors.remove(sensor);
	}

	/**
	 * Gets all the sensors registered for this context source
	 * @return
	 */
	public Iterator<Sensor> getSensors() {
		return sensors.iterator();
	}
}
