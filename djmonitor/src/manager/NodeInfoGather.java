package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parser.ProcParser;
import parser.SensorParser;
import usage.CpuData;
import usage.DiskData;
import usage.MemData;
import usage.MonitoredData;
import usage.NetworkData;
import usage.SensorData;
import usage.UsageType;
import utils.Utils;

/**
 * 
 * @author pmdusso
 */
public enum NodeInfoGather
{

	INSTANCE;

	/**
	 * Collect process system information from /proc file system.
	 * 
	 * @param: uuid: universally unique identifier of the node in the cluster.
	 * 
	 */
	public synchronized MonitoredData getSystemUsage(int _uuid, String _sensorAddress)
	{
		MonitoredData mData = null;
		SensorData sData = null;

		if (Utils.stringNotEmpty(_sensorAddress))
		{
			sData = getSensorData(_sensorAddress);
		}
		
		ProcParser pp = new ProcParser(Utils.getPid());

		System.out
				.println("IP Address of the client: " + String.valueOf(_uuid));
		mData = new MonitoredData(_uuid,
				fillCpuData(pp.gatherUsage(UsageType.CPU)),
				fillMemData(pp.gatherUsage(UsageType.MEMORY)),
				fillDiskData(pp.gatherUsage(UsageType.DISK)),
				fillNetworkData(pp.gatherUsage(UsageType.NETWORK)), sData);

		return mData;
	}

	/**
	 * Collect machine physical information from a sensor installed on it.
	 * 
	 * @param: uuid: universally unique identifier of the node in the cluster.
	 *         ATENTION: the uuid *must be* the uuid of the sensored machine,
	 *         not the sensor!
	 */
	public synchronized SensorData getSensorData(String _sensorAddress)
	{
		// TODO: getSensorData method

		SensorParser sp = new SensorParser();

		return fillSensorData(sp.gatherSensor());
	}

	/**
	 * Create a sensor data object, with the informatation polled from the
	 * sensor
	 */
	private SensorData fillSensorData(ArrayList<String> gatheredData)
	{
		// TODO: fillSensorData method

		return null;
	}

	/**
	 * Creates a list of CPU objects; Each objects of this list correspond to a
	 * CPU core. The first is the sum of all cores.
	 */
	private Map<Integer, CpuData> fillCpuData(ArrayList<String> gatheredData)
	{
		Map<Integer, CpuData> c = new HashMap<Integer, CpuData>();
		// c.add(new CpuData(coreId, user, nice, sysmode, idle, iowait, irq,
		// softirq, steal, guest));
		int offset = 10;
		for (int base = 0; base < gatheredData.size(); base += offset)
		{
			c.put(Integer.parseInt(gatheredData.get(base)),
					new CpuData(Integer.parseInt(gatheredData.get(base)),
							Integer.parseInt(gatheredData.get(base + 1)), Long
									.parseLong(gatheredData.get(base + 2)),
							Long.parseLong(gatheredData.get(base + 3)), Long
									.parseLong(gatheredData.get(base + 4)),
							Long.parseLong(gatheredData.get(base + 5)), Long
									.parseLong(gatheredData.get(base + 6)),
							Integer.parseInt(gatheredData.get(base + 7)), Long
									.parseLong(gatheredData.get(base + 8)),
							Long.parseLong(gatheredData.get(base + 9))));
		}
		return c;
	}

	/**
	 * Creates a single Memory object corresponding to all values read from the
	 * system.
	 */
	private MemData fillMemData(List<String> gatheredData)
	{
		MemData m = new MemData(
				Integer.parseInt(gatheredData.get(0)),
				Integer.parseInt(gatheredData.get(1)),
				Integer.parseInt(gatheredData.get(2)),
				Integer.parseInt(gatheredData.get(3)),
				// Integer.parseInt(gatheredData.get(4)), not used
				Integer.parseInt(gatheredData.get(5)),
				// Integer.parseInt(gatheredData.get(6)), not used
				gatheredData.get(7),
				Integer.parseInt(gatheredData.get(8)),
				gatheredData.get(9),
				Integer.parseInt(gatheredData.get(10)),
				// used memory = total memory - free memory
				(Integer.parseInt(gatheredData.get(10)) - Integer
						.parseInt(gatheredData.get(11))),
				Integer.parseInt(gatheredData.get(11)),
				Integer.parseInt(gatheredData.get(12)),
				Integer.parseInt(gatheredData.get(13)));
		return m;
	}

	/**
	 * Creates a list of Disk objects; Each object of this list correspond to a
	 * disk physical partition.
	 */
	private Map<String, DiskData> fillDiskData(List<String> gatheredData)
	{

		Map<String, DiskData> d = new HashMap<String, DiskData>();
		int offset = 14;

		for (int base = 0; base < gatheredData.size(); base += offset)
		{
			d.put(gatheredData.get(base + 2),
					new DiskData(gatheredData.get(base), gatheredData
							.get(base + 1), gatheredData.get(base + 2), Long
							.parseLong(gatheredData.get(base + 3)), Long
							.parseLong(gatheredData.get(base + 4)), Long
							.parseLong(gatheredData.get(base + 5)), Long
							.parseLong(gatheredData.get(base + 6)), Long
							.parseLong(gatheredData.get(base + 7)), Long
							.parseLong(gatheredData.get(base + 8)), Long
							.parseLong(gatheredData.get(base + 9)), Long
							.parseLong(gatheredData.get(base + 10)), Long
							.parseLong(gatheredData.get(base + 11)), Long
							.parseLong(gatheredData.get(base + 12)), Long
							.parseLong(gatheredData.get(base + 13))));
		}

		return d;
	}

	/**
	 * Creates a list of Network objects; Each object of this list correspond to
	 * a network interface.
	 */
	private Map<String, NetworkData> fillNetworkData(List<String> gatheredData)
	{
		Map<String, NetworkData> n = new HashMap<String, NetworkData>();
		int offset = 17;
		for (int base = 0; base < gatheredData.size(); base += offset)
		{
			n.put(gatheredData.get(base),
					new NetworkData(gatheredData.get(base), Long
							.parseLong(gatheredData.get(base + 1)), Integer
							.parseInt(gatheredData.get(base + 2)), Integer
							.parseInt(gatheredData.get(base + 3)), Integer
							.parseInt(gatheredData.get(base + 4)), Integer
							.parseInt(gatheredData.get(base + 5)), Integer
							.parseInt(gatheredData.get(base + 6)), Integer
							.parseInt(gatheredData.get(base + 7)), Integer
							.parseInt(gatheredData.get(base + 8)), Long
							.parseLong(gatheredData.get(base + 9)), Integer
							.parseInt(gatheredData.get(base + 10)), Integer
							.parseInt(gatheredData.get(base + 11)), Integer
							.parseInt(gatheredData.get(base + 12)), Integer
							.parseInt(gatheredData.get(base + 13)), Integer
							.parseInt(gatheredData.get(base + 14)), Integer
							.parseInt(gatheredData.get(base + 15)), Integer
							.parseInt(gatheredData.get(base + 16))));
		}
		return n;
	}
}
