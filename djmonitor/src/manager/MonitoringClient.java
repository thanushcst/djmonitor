package manager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;
import usage.NetworkData;
import utils.Utils;

public class MonitoringClient
{

	public MonitoringClient(long _gatherInterval, String _servAddress, int _servPort)
	{
		new Gather(this, _gatherInterval);
		new Sender(this, _servAddress, _servPort);
	}

	MonitoredData mData;
	boolean valueSet = false;

	synchronized MonitoredData get()
	{
		if (!valueSet)
		{
			try
			{
				wait();
			} catch (InterruptedException e)
			{
				System.out.println("InterruptedException caught");
			}
		}
		System.out.println("Available data being used.");
		valueSet = false;
		notify();
		return this.mData;
	}

	synchronized void put(MonitoredData data)
	{
		if (valueSet)
		{
			try
			{
				wait();
			} catch (InterruptedException e)
			{
				System.out.println("InterruptedException caught");
			}
		}
		this.mData = data;
		valueSet = true;
		System.out.println("Gathered data available.");
		notify();
	}
}

class Gather implements Runnable
{

	MonitoringClient client;
	long gatherInterval = 0;
	int uuid = 0;

	Gather(MonitoringClient client, long _gatherInterval)
	{
		this.client = client;
		this.gatherInterval = _gatherInterval;
		this.uuid = Utils.getNodeUUID();
		new Thread(this, "Gather").start();
	}

	@Override
	public void run()
	{
		MonitoredData tempData;
		while (true)
		{
			try
			{
				Thread.sleep(this.gatherInterval);
			} catch (InterruptedException ex)
			{
				Logger.getLogger(MonitoringClient.class.getName()).log(Level.SEVERE, null, ex);
			}
			tempData = NodeInfoGather.INSTANCE.getSystemUsage(this.uuid);
			System.out.println("Finished gathering data from procfs. Ready to send it.");
			client.put(tempData);
		}
	}
}

class Sender implements Runnable
{
	// Server name or IP address
	String server;
	// Convert argument String to bytes using the default character encoding
	byte[] data;
	// Port to connect to the server
	int servPort = 0;
	// Create socket that is connected to server on specified port
	Socket socket;
	// Count the number of packages sent
	int packageCount = 0;

	ObjectOutputStream oos;
	MonitoringClient client;

	Sender(MonitoringClient _client, String _server, int _servPort)
	{
		// check for parameters
		if (_client == null)
			throw new IllegalArgumentException("Parameter: <Client object> null.");
		if (!Utils.stringNotEmpty(_server))
			throw new IllegalArgumentException("Parameter: <Server Address> empty.");
		if (_servPort <= 0)
			throw new IllegalArgumentException("Parameter: <Server Port> empty.");

		this.client = _client;
		this.server = _server;
		this.servPort = _servPort;

		try
		{
			socket = new Socket(server, servPort);
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Connected to server...");
		} catch (UnknownHostException e)
		{
			Logger.getLogger(MonitoringClient.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e)
		{
			Logger.getLogger(MonitoringClient.class.getName()).log(Level.SEVERE, null, e);
		}

		new Thread(this, "Sender").start();
	}

	@Override
	public void run()
	{
		MonitoredData tempData;

		while (true)
		{
			if ((tempData = client.get()) == null)
				throw new IllegalArgumentException("Parameter: <Monitored Data> empty.");

			// printMonitoredData(tempData);
			System.out.println("Data gathered. Sending...");
			try
			{
				oos.reset();
				oos.writeUnshared(tempData);
				oos.flush();
			} catch (IOException e)
			{
				Logger.getLogger(MonitoringClient.class.getName()).log(Level.SEVERE, null, e);
			}
			this.packageCount++;
			System.out.println("Client sent the monitored data package: " + String.valueOf(this.packageCount));
		}
	}

	/**
	 * @param tempData
	 */
	@SuppressWarnings("unused")
	private void printMonitoredData(MonitoredData tempData)
	{
		for (CpuData o : tempData.getCpu())
		{
			System.out.print("CPU:");
			System.out.println(o.toString());
		}
		for (DiskData o : tempData.getDisk())
		{
			System.out.print("DISK:");
			System.out.println(o.toString());
		}
		for (NetworkData o : tempData.getNet())
		{
			System.out.print("NETWORK:");
			System.out.println(o.toString());
		}
		System.out.print("MEMORY:");
		System.out.println(tempData.getMem().toString());
	}

	protected void finalize() throws Throwable
	{
		// do finalization here
		// Close the socket and its streams
		socket.close();
		oos.close();

		super.finalize();
	}
}