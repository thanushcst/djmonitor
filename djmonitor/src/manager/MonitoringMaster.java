package manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import parser.ProcParser;
import storage.HistoricalDatabase;
import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;

public class MonitoringMaster implements Runnable
{

	// Socket connect to client
	private Socket clientSock;
	// Server logger
	private Logger logger;
	// Historical database to save the monitored data.
	private HistoricalDatabase hdb;
	// Get the input and output I/O streams from socket
	private ObjectInputStream ois;
	/*
	 * Global object to keep state between gathers. This circular buffer works
	 * as the following: 1) it is a first in first out buffer with a fixed size
	 * that replaces its oldest element if full. 2) The removal order of a
	 * CircularFifoBuffer is based on the insertion order; elements are removed
	 * in the same order in which they were added.
	 */
	private Buffer bufPreviouslyData;
	private Boolean isFirstIteration;
	// Monitor gather interval, in seconds
	private int gatherInterval = 0;

	public MonitoringMaster(int gtrInterval, Socket clntSock, HistoricalDatabase _hdb)
	{
		this.clientSock = clntSock;
		this.hdb = _hdb;
		this.bufPreviouslyData = new CircularFifoBuffer(12);
		this.isFirstIteration = true;
		this.gatherInterval = gtrInterval / 1000;

		try
		{
			ois = new ObjectInputStream(this.clientSock.getInputStream());
		} catch (IOException e)
		{
			logger.log(Level.WARNING, "Exception in echo protocol", e);
		}
	}

	public void handleEchoClient(Socket client, Logger logger)
	{
		try

		{
			MonitoredData mdata;
			// Receive until client closes connection
			while ((mdata = (MonitoredData) ois.readObject()) != null)
			{
				System.out.println("Got received data. Ready to save.");
				//this.hdb.saveOrUpdate(mdata);
				System.out.println("Monitored Data arrived at home from node: " + String.valueOf(mdata.getNodeID()));
				calculateUsage(mdata);
			}

		} catch (IOException ex)
		{
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex)
		{
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			try
			{
				client.close();
			} catch (IOException e)
			{
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void calculateUsage(MonitoredData _actualData)
	{
		if (!isFirstIteration)
		{
			printCpu(_actualData.getCpu(), ((MonitoredData) this.bufPreviouslyData.get()).getCpu());

			printDisk(_actualData.getDisk(), 10);

			/* # Numero de acessos/leituras na memoria */

			/* # taxa (recebidos/transmitidos)/total */
		} else
			this.isFirstIteration = false;

		// Add one more monitored data object to buffer
		this.bufPreviouslyData.add(_actualData);

	}

	/*
	 * # Nos ultimo t de tempo qual a intensidade de uso do disco (ms fazendo
	 * IO/total ms transcorrido) Percentual ou numero de requisicoes
	 */
	private void printDisk(List<DiskData> lstActualDisk, int interval)
	{
		/*
		 * Teste se tem elementos suficientes coletados para fazer a analize do
		 * intervalo de tempo desejado.
		 */
		if (this.bufPreviouslyData.size() < (interval / this.gatherInterval))
			return;

		Buffer temp = this.bufPreviouslyData;

		DiskData mainDiskPreviously;
		DiskData mainDiskActual;

		for (int i = 0; i < (interval / this.gatherInterval) - 1; i++)
		{
			temp.get();
		}
		mainDiskPreviously = getMainDiskDataPartition(((MonitoredData) temp.get()).getDisk(), "sda1");
		mainDiskActual = getMainDiskDataPartition(lstActualDisk, "sda1");

		long diskIoIntensity = (mainDiskActual.getMillisecondsSpentInIO() - mainDiskPreviously.getMillisecondsSpentInIO()) / interval;

		System.out.println("Disk I/O Intensity. ");
		System.out.println("Main Partition name: " + "sda1");
		System.out.println("I/O value          : " + String.valueOf(diskIoIntensity));

	}

	/**
	 * @return
	 */
	private DiskData getMainDiskDataPartition(List<DiskData> lstDisk, String partitionName)
	{
		for (DiskData d : lstDisk)
		{
			if (d.getName().equals(partitionName))
				return d;
		}
		return null;
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
	private void printCpu(List<CpuData> lstActualCpu, List<CpuData> lstPreviouslyCpu)
	{
		long totalDiff;
		long idleDiff;
		long usage = 0;

		System.out.println("CPU Usage per core.");
		for (CpuData core : lstActualCpu)
		{
			totalDiff = core.getTotalTimes() - lstPreviouslyCpu.get(core.getCoreId()).getTotalTimes();
			idleDiff = core.getIdle() - lstPreviouslyCpu.get(core.getCoreId()).getIdle();
			if (totalDiff != 0)
				usage = (1000 * (totalDiff - idleDiff) / totalDiff + 5) / 10;

			System.out.println("Core number: " + String.valueOf(core.getCoreId()));
			System.out.println("Core %     : " + String.valueOf(usage));
		}
	}

	@Override
	public void run()
	{
		handleEchoClient(clientSock, logger);
	}

}
