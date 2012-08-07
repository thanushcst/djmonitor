package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import storage.HistoricalDatabase;
import utils.Utils;

/**
 * 
 * @author pmdusso
 */
public class Monitor
{

	private static final int GATHER_INTERVAL = 5000;

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1 || args.length > 4)
		{
			System.out.println("Use:");
			System.out.println("monitor -m <PORT> para rodar MonitoringMaster");
			System.out
					.println("monitor -c <PORT> <SERVER_ADDRESS> para rodar MonitoringClient sem sensor");
			System.out
					.println("monitor -c <PORT> <SERVER_ADDRESS> <SENSOR_ADDRESS> <CHANNELS> para rodar MonitoringClient com sensor");
		} else
		{
			if (args[0].equals("-m"))
				startMonitoringMasterService(getServerPortFromArgs(args[1]));
			else if (args[0].equals("-c"))
				startMonitoringClientService(getServerAddressFromArgs(args[2]),
						getServerPortFromArgs(args[1]),
						getSensorAddressFromArgs(args[3]),
						getSensorNumberOfChannels(args[4]));
			System.out.println("Press Control-C to stop.");
		}
	}

	private static int getSensorNumberOfChannels(String _channels)
	{
		if (!Utils.stringNotEmpty(_channels))
		{
			System.out
					.println("No specific number of channels received. Using default value 4.");
			return 4;
		} else
			return Integer.parseInt(_channels);
	}

	private static String getSensorAddressFromArgs(String _sensorAddress)
	{
		if (!Utils.stringNotEmpty(_sensorAddress))
		{
			System.out
					.println("No sensor address received. Monitoring only system usage...");
			return null;
		} else
			return _sensorAddress;
	}

	/**
	 * @param argAddress
	 * @param ipmask
	 * @param serverAddress
	 * @return
	 * @throws IllegalArgumentException
	 */
	private static String getServerAddressFromArgs(String argAddress) throws IllegalArgumentException
	{
		if (!Utils.stringNotEmpty(argAddress))
			throw new IllegalArgumentException(
					"Parameter: <Server Address> empty or wrong format.");
		else
			return argAddress;
	}

	/**
	 * @param argPort
	 *            : the argument port received as parameter.
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	private static int getServerPortFromArgs(String argPort) throws IllegalArgumentException, NumberFormatException
	{
		if (!Utils.stringNotEmpty(argPort))
			throw new IllegalArgumentException(
					"Parameter: <Server Port> empty.");
		else
			return Integer.valueOf(argPort);
	}

	/**
	 * Monitoring Client Service
	 * 
	 * @param _serverAddress
	 * @param _serverPort
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	private static void startMonitoringClientService(String _serverAddress, int _serverPort, String _sensorAddress, int _channels) throws IllegalArgumentException, NumberFormatException
	{
		new MonitoringClient(GATHER_INTERVAL, _serverAddress, _serverPort,
				_sensorAddress, _channels);
	}

	/**
	 * Monitoring Master Service
	 * 
	 * @param _serverPort
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void startMonitoringMasterService(int _serverPort) throws IllegalArgumentException, NumberFormatException, IOException
	{

		// Create a server socket to accept client connection requests
		System.out.println("Creating master service...");
		final ServerSocket servSock = new ServerSocket(_serverPort);
		final HistoricalDatabase hdb = new HistoricalDatabase("h.db");

		final Logger logger = Logger.getLogger("pratical");
		int totalClientsConnected = 0;

		// Run forever, accepting and spawning a thread for each connection
		while (true)
		{
			System.out
					.println(String
							.format("Waiting for connections. Right now %d clients connected. ",
									totalClientsConnected));
			// Block waiting for connection
			final Socket clntSock = servSock.accept();
			totalClientsConnected++;
			final Thread thread = new Thread(new MonitoringMaster(
					GATHER_INTERVAL, clntSock, hdb));
			thread.start();
			logger.info("Created and started Thread " + thread.getName());
		}
		/* NOT REACHED */
	}
}