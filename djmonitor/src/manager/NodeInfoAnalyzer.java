package manager;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import parser.ProcParser;

import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;
import usage.NetworkData;
import utils.Utils;

public class NodeInfoAnalyzer
{
	private Buffer bufPreviouslyData;
	private int gatherInterval;
	private int networkAdapterCapacity;

	public NodeInfoAnalyzer(int gatherInterval)
	{
		this.bufPreviouslyData = new CircularFifoBuffer(12);
		this.gatherInterval = gatherInterval / 1000;
		this.networkAdapterCapacity = Utils.getNetworkAdapterCapacity();
	}

	/**
	 * Analyze the collected data.
	 * 
	 * @CPU: CPU load, compared to the last monitored object.
	 * @DISK: Intensity of I/O access during the specified interval.
	 * @Network: Tax of packages sent and received, compared to the total
	 *           bandwidth of the network adapter.
	 * @Memory: Number of reads and writes in the memory.
	 */
	@SuppressWarnings("unchecked")
	public void analyze(MonitoredData _actualData, int _interval)
	{
		/*
		 * Test if there is enough elements collected to analyze for the desired
		 * interval
		 */
		if (this.bufPreviouslyData.size() < (_interval / this.gatherInterval))
		{
			System.out.println("Actual buffer size: " + String
					.valueOf(this.bufPreviouslyData.size()));
			System.out.println("Actual interval   : " + String
					.valueOf(this.gatherInterval));
		} else
		{
			try
			{
				printCpu(_actualData.getCpu(), _interval);
				printDisk(_actualData.getDisk(), _interval);
				printNetwork(_actualData.getNet(), _interval);
				
			} catch (Exception e)
			{
				Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
						null, e);
			}
		}

		// Add one more monitored data object to buffer
		this.bufPreviouslyData.add(_actualData);
	}

	private void printNetwork(Map<String, NetworkData> mapActualNetwork, int _interval)
	{
		Map<String, NetworkData> mapPreviouslyNetwork;

		mapPreviouslyNetwork = getMonitoredDataObjectForInterval(_interval)
				.getNet();

		System.out.println("Network Usage per interface.");
		for (String netInterface : mapActualNetwork.keySet())
		{
			System.out.println("   Interface Name: " + String
					.valueOf(netInterface));
			System.out.println("   Interface %   : " + String
					.valueOf(calculateNetUsage(
							mapActualNetwork.get(netInterface),
							mapPreviouslyNetwork.get(netInterface),
							this.networkAdapterCapacity)));
		}
	}

	private String calculateNetUsage(NetworkData networkDataActual, NetworkData networkDataPreviously, int networkAdapterCapacity)
	{
		return String
				.valueOf((networkDataActual.getReceive().getRX_Bytes() - networkDataPreviously
						.getReceive().getRX_Bytes()) / networkAdapterCapacity);
	}

	/*
	 * Disk I/O intensity (ms doing I/O during a total ms time interval)
	 */
	private void printDisk(Map<String, DiskData> mapActualDisk, int _interval)
	{
		Map<String, DiskData> mainDiskPreviously = getMonitoredDataObjectForInterval(
				_interval).getDisk();
		DiskData mainDiskActual = mapActualDisk.get("sda1");

		System.out.println("Disk I/O Intensity. ");
		System.out.println("   Main Partition name: " + "sda1");
		System.out.println("   I/O value          : " + String
				.valueOf(calculateDiskUsage(_interval,
						mainDiskActual.getMillisecondsSpentInIO(),
						mainDiskPreviously.get("sda1").getMillisecondsSpentInIO())));

	}

	/**
	 * @param _interval
	 * @param mainDiskPreviously
	 * @param mainDiskActual
	 * @return
	 */
	private long calculateDiskUsage(int _interval, long actualMsInIo, long previouslyMsInIo)
	{
		return (actualMsInIo - previouslyMsInIo) / _interval;
	}

	/**
	 * The following operations are executed for each core of the processor and
	 * for the virtual core number 0, which is the sum of all CPU cores times.
	 * 
	 * Subtracts the total CPU times from the current monitored CPU data object
	 * from the previously one. Then, subtracts the idle time from the current
	 * monitored CPU data object from the previously one. After calculate both
	 * differences, calculate the usage for the core by subtracting the idle
	 * time from the total time and dividing by the total time.
	 */
	private void printCpu(Map<Integer, CpuData> mapActualCpu, int _interval)
	{
		Map<Integer, CpuData> cpuPreviously = getMonitoredDataObjectForInterval(
				_interval).getCpu();
		System.out.println("CPU Usage per core.");
		for (int core = 0; core < mapActualCpu.keySet().size(); core++)
		{
			System.out.println("   Core number: " + String.valueOf(core));
			System.out.println("   Core %     : " + calculateCpuUsage(
					mapActualCpu, cpuPreviously, core));
		}
	}

	/**
	 * @param mapActualCpu
	 *            : The collection with the least gathered CPU monitored data
	 * @param mapPreviouslyCpu
	 *            : The collection with the immediately previously gathered CPU
	 *            monitored data
	 * @param core
	 *            : the number of the core. A "0" value means that is sum off
	 *            all cores
	 * @return the usage tax value
	 */
	private String calculateCpuUsage(Map<Integer, CpuData> mapActualCpu, Map<Integer, CpuData> mapPreviouslyCpu, int core)
	{
		long totalDiff;
		long idleDiff;
		totalDiff = mapActualCpu.get(core).getTotalTimes() - mapPreviouslyCpu
				.get(core).getTotalTimes();
		idleDiff = mapActualCpu.get(core).getIdle() - mapPreviouslyCpu
				.get(core).getIdle();

		return String.valueOf(calculateCpuUsageTax(totalDiff, idleDiff));
	}

	/**
	 * @param totalDiff
	 *            : The total (sum of all CPU times) difference time between the
	 *            two CPU clock measures
	 * @param idleDiff
	 *            : The total idle difference time between the two CPU clock
	 *            measures
	 * @return the usage tax
	 */
	private long calculateCpuUsageTax(long totalDiff, long idleDiff)
	{
		if (totalDiff != 0)
			return (1000 * (totalDiff - idleDiff) / totalDiff + 5) / 10;
		else
			return -1;
	}

	/**
	 * To analyze some component during a specific time interval, we must
	 * compare the current monitored data object to the one collected the
	 * interval seconds before.
	 * 
	 * This method returns the monitored data object gathered interval seconds
	 * before.
	 */
	private MonitoredData getMonitoredDataObjectForInterval(int interval)
	{
		Buffer temp = this.bufPreviouslyData;

		for (int i = 0; i < (interval / this.gatherInterval) - 1; i++)
		{
			temp.get();
		}
		return (MonitoredData) temp.get();
	}
}