package manager;

import java.util.Map;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;
import usage.NetworkData;

public class NodeInfoAnalyzer
{
	public Buffer bufPreviouslyData;
	public Boolean isFirstIteration;
	public int gatherInterval;

	public NodeInfoAnalyzer(int gatherInterval)
	{
		this.bufPreviouslyData = new CircularFifoBuffer(12);
		this.isFirstIteration = true;
		this.gatherInterval = gatherInterval / 1000;
	}

	/**
	 * Analyze the collected data.
	 * 
	 * @CPU: CPU load, compared to the last monitored object
	 * @DISK: Intensity of I/O access during the specified interval
	 * @Network: Tax of packages sent and received, compared to the total
	 *           bandwidth of the network adapter
	 */
	public void analyze(MonitoredData _actualData, int _interval)
	{
		if (!this.isFirstIteration)
		{
			printCpu(_actualData.getCpu(), getMonitoredDataObjectForInterval(5000).getCpu());

			// TODO: Numero de acessos/leituras na memoria.

			/*
			 * Test if there is enough elements collected already to analyze for
			 * the desired interval
			 */
			if (this.bufPreviouslyData.size() < (_interval / this.gatherInterval))
				return;

			printDisk(_actualData.getDisk(), _interval);
			printNetwork(_actualData.getNet(), _interval);

		} else
			this.isFirstIteration = false;

		// Add one more monitored data object to buffer
		this.bufPreviouslyData.add(_actualData);

	}

	private void printNetwork(Map<String, NetworkData> mapNetwork, int _interval)
	{
		NetworkData mainInterfacePreviously;
		NetworkData mainInterfaceActual;

		mainInterfacePreviously = getMonitoredDataObjectForInterval(_interval).getNet().get("eth1");
		mainInterfaceActual = mapNetwork.get("eth1");

		// Found a way to get the maximum bandwidth of the network card...
		getNetworkAdapterMaxBandwidth();
	}

	private void getNetworkAdapterMaxBandwidth()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * # Nos ultimo t de tempo qual a intensidade de uso do disco (ms fazendo
	 * IO/total ms transcorrido) Percentual ou numero de requisicoes
	 */
	private void printDisk(Map<String, DiskData> mapActualDisk, int _interval)
	{
		DiskData mainDiskPreviously;
		DiskData mainDiskActual;

		mainDiskPreviously = getMonitoredDataObjectForInterval(_interval).getDisk().get("sda1");
		mainDiskActual = mapActualDisk.get("sda1");

		long diskIoIntensity = (mainDiskActual.getMillisecondsSpentInIO() - mainDiskPreviously.getMillisecondsSpentInIO()) / _interval;

		System.out.println("Disk I/O Intensity. ");
		System.out.println("Main Partition name: " + "sda1");
		System.out.println("I/O value          : " + String.valueOf(diskIoIntensity));

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
	private void printCpu(Map<Integer, CpuData> mapActualCpu, Map<Integer, CpuData> mapPreviouslyCpu)
	{
		for (int core = 0; core < mapActualCpu.keySet().size(); core++)
		{
			System.out.println("CPU Usage per core.");
			System.out.println("Core number: " + String.valueOf(core));
			System.out.println("Core %     : " + String.valueOf(calculateCpuUsage(mapActualCpu, mapPreviouslyCpu, core)));
		}
	}

	/**
	 * @param mapActualCpu
	 * @param mapPreviouslyCpu
	 * @param core
	 *            : the number of the core. A "0" value means that is sum off
	 *            all cores
	 * @return the usage tax value
	 */
	private long calculateCpuUsage(Map<Integer, CpuData> mapActualCpu, Map<Integer, CpuData> mapPreviouslyCpu, int core)
	{
		long totalDiff;
		long idleDiff;
		totalDiff = mapActualCpu.get(core).getTotalTimes() - mapPreviouslyCpu.get(core).getTotalTimes();
		idleDiff = mapActualCpu.get(core).getIdle() - mapPreviouslyCpu.get(core).getIdle();
		if (totalDiff != 0)
			return (1000 * (totalDiff - idleDiff) / totalDiff + 5) / 10;
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