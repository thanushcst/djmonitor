/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usage;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author pmdusso
 */
public class MonitoredData implements Serializable
{

	/**
     *
     */
	private static final long serialVersionUID = 7909922150038205539L;
	private int nodeID;
	private Map<Integer, CpuData> cpu;
	private MemData mem;
	private Map<String, DiskData> disk;
	private Map<String, NetworkData> net;
	private SensorData sensor;

	public MonitoredData(int _uuid, Map<Integer, CpuData> _cpu, MemData _mem,
			Map<String, DiskData> _disk, Map<String, NetworkData> _net,
			SensorData _sensor)
	{
		if (_cpu == null)
			throw new ExceptionInInitializerError(
					"CPU monitored data object is null");
		else if (_mem == null)
			throw new ExceptionInInitializerError(
					"Memory monitored data object is null");
		else if (_disk == null)
			throw new ExceptionInInitializerError(
					"Disk monitored data object is null");
		else if (_net == null)
			throw new ExceptionInInitializerError(
					"Network monitored data object is null");
		else if (_sensor == null)
			throw new ExceptionInInitializerError(
					"Sensor monitored data object is null");

		this.nodeID = _uuid;

		this.cpu = _cpu;
		this.mem = _mem;
		this.disk = _disk;
		this.net = _net;
		this.sensor = _sensor;
	}

	public int getNodeID()
	{
		return this.nodeID;
	}

	/**
	 * @return the cpu
	 */
	public Map<Integer, CpuData> getCpu()
	{
		return this.cpu;
	}

	/**
	 * @return the mem
	 */
	public MemData getMem()
	{
		return this.mem;
	}

	/**
	 * @return the disk
	 */
	public Map<String, DiskData> getDisk()
	{
		return this.disk;
	}

	/**
	 * @return the net
	 */
	public Map<String, NetworkData> getNet()
	{
		return this.net;
	}

	/**
	 * @return the sensor
	 */
	public SensorData getSensor()
	{
		return sensor;
	}
}
