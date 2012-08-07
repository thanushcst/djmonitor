package usage;

import java.io.Serializable;

/**
 * @author pmdusso
 * @version 1.0 @created 07-ago-2012
 */
public class SensorData implements Serializable
{

	/**
	 * This is necessary to serialize the object before sending through sockets
	 */
	private static final long serialVersionUID = -5576168050062983937L;

	/**
	 * Construct a new Sensor Data object.
	 * 
	 * @param temperature
	 *            : the temperature measured by the device from a computer
	 * @param humidity
	 *            : the humidity measured by the device from a computer
	 * @param voltage
	 *            : the voltage measured by the device from a computer
	 */
	public SensorData(double _temperature, double _humidity, double _voltage)
	{
		this.temperature = _temperature;
		this.humidity = _humidity;
		this.voltage = _voltage;
	}

	@Override
	public String toString()
	{
		return "SensorData [temperature=" + temperature + ", humidity=" + humidity + ", voltage=" + voltage + "]";
	}

	private double temperature;
	private double humidity;
	private double voltage;

	@Override
	public void finalize() throws Throwable
	{
	}

	/**
	 * @return the temperature
	 */
	public double getTemperature()
	{
		return temperature;
	}

	/**
	 * @return the humidity
	 */
	public double getHumidity()
	{
		return humidity;
	}

	/**
	 * @return the voltage
	 */
	public double getVoltage()
	{
		return voltage;
	}
}
