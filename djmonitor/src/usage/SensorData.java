package usage;

import java.io.Serializable;
import java.util.Map;

import org.joda.time.DateTime;

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
	public SensorData(DateTime _date, Map<UnityType, String> _values)
	{
		this.date = _date;

		for (UnityType u : _values.keySet())
		{
			switch (u)
			{
			case CELCIUS:
				this.temperatureUnity = u;
				this.temperature = Double.parseDouble(_values.get(u));
				break;
			case WATT:
				this.voltageUnity = u;
				this.voltage = Double.parseDouble(_values.get(u));
				break;
			case PERCENT:
				this.humidityUnity = u;
				this.humidity = Double.parseDouble(_values.get(u));
				break;
			case NULL:
				// TODO: Fourth channel, still have to be properly treated
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String toString()
	{
		return "SensorData [temperature=" + temperature + ", humidity=" + humidity + ", voltage=" + voltage + "]";
	}

	private double temperature;
	private UnityType temperatureUnity;
	private double humidity;
	private UnityType humidityUnity;
	private double voltage;
	private UnityType voltageUnity;
	private DateTime date;

	@Override
	public void finalize() throws Throwable
	{
		/*
		 * DATE 12/08/07 TIME 17:05:26 N 001 ^C +00219E-01 N 002 W +00000E-01 N
		 * 003 % -00250E-01 N 004 -00150E-01
		 */

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

	public UnityType getTemperatureUnity()
	{
		return temperatureUnity;
	}

	public UnityType getHumidityUnity()
	{
		return humidityUnity;
	}

	public UnityType getVoltageUnity()
	{
		return voltageUnity;
	}

	public DateTime getDate()
	{
		return date;
	}
}
