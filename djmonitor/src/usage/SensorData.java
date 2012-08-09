package usage;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

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
	 */
	public SensorData(String _sensorAddress, DateTime _date, List<SimpleEntry<UnityType, Double>> _values)
	{
		this.sensorUUID = Math.abs(_sensorAddress.hashCode());
		this.date = _date;
		this.channels.addAll(_values);
	}

	private int sensorUUID;
	private DateTime date;
	private ArrayList<SimpleEntry<UnityType, Double>> channels;

	
	
	@Override
	public String toString()
	{
		return "SensorData [sensorUUID=" + sensorUUID + ", date=" + date + ", channels=" + channelsToString() + "]";
	}

	private String channelsToString()
	{
		return "";
	}

	@Override
	public void finalize() throws Throwable
	{

	}
	
	public int SensorUUID()
	{
		return sensorUUID;
	}

	public List<SimpleEntry<UnityType, Double>> getChannels()
	{
		return channels;
	}

	public DateTime getDate()
	{
		return date;
	}
}
