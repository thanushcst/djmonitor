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

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1 || args.length > 3)
		{
			System.out.println("Use:");
			System.out.println("monitor -m <PORT> para rodar MonitoringMaster");
			System.out.println("monitor -c <PORT> <SERVER_ADDRESS> para rodar MonitoringClient");
		} else
		{
			if (args[0].equals("-m"))
			{
				startMonitoringMasterService(getServerPortFromArgs(args[1]));
			} else if (args[0].equals("-c"))
			{
				startMonitoringClientService(getServerAddressFromArgs(args[2]), getServerPortFromArgs(args[1]));
			}
			System.out.println("Press Control-C to stop.");
		}
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
		String ipmask = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

		if (!Utils.stringNotEmpty(argAddress) || !ipmask.matches(argAddress))
			throw new IllegalArgumentException("Parameter: <Server Address> empty or wrong format.");
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
			throw new IllegalArgumentException("Parameter: <Server Port> empty.");
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
	private static void startMonitoringClientService(String _serverAddress, int _serverPort) throws IllegalArgumentException, NumberFormatException
	{
		new MonitoringClient(5000, _serverAddress, _serverPort);
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
		ServerSocket servSock = new ServerSocket(_serverPort);
		HistoricalDatabase hdb = new HistoricalDatabase("h.db");

		Logger logger = Logger.getLogger("pratical");
		int totalClientsConnected = 0;

		// Run forever, accepting and spawning a thread for each connection
		while (true)
		{
			System.out.println(String.format("Waiting for connections. Right now %d clients connected. ", totalClientsConnected));
			// Block waiting for connection
			Socket clntSock = servSock.accept();
			totalClientsConnected++;
			Thread thread = new Thread(new MonitoringMaster(clntSock, hdb));
			thread.start();
			logger.info("Created and started Thread " + thread.getName());
		}
		/* NOT REACHED */
	}
}