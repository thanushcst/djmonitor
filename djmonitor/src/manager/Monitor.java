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
public class Monitor {

	public static void main(String[] args) throws IOException {
		String ipmask = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

		// Server address
		String serverAddress = null;
		// Server port. Use 4545 in case no port is passed.
		int serverPort = 4545;

		if (args.length < 1) {
			System.out.println("Use:");
			System.out.println("monitor -m <PORT> para rodar MonitoringMaster");
			System.out.println("monitor -c <SERVER_ADDRESS> <PORT> para rodar MonitoringClient");
		} else {

			if (args[0].equals("-m")) {
				if (!Utils.stringNotEmpty(args[1]))
					throw new IllegalArgumentException("Parameter: <Server Port> empty.");
				else
					startMonitoringMasterService(Integer.valueOf(args[1]));
			} else if (args[0].equals("-c")) {
				if (!Utils.stringNotEmpty(args[1]))
					throw new IllegalArgumentException("Parameter: <Server Address> empty.");
				else
					serverAddress = args[1];

				if (!Utils.stringNotEmpty(args[2]))
					throw new IllegalArgumentException("Parameter: <Server Port> empty.");
				else
					serverPort = Integer.valueOf(args[2]);

				startMonitoringClientService(serverAddress, serverPort);
			}

			System.out.println("Press Control-C to stop.");
		}
	}

	/**
	 * Monitoring Client Service
	 * 
	 * @param _serverAddress
	 * @param _serverPort
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	private static void startMonitoringClientService(String _serverAddress, int _serverPort) throws IllegalArgumentException, NumberFormatException {
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
	private static void startMonitoringMasterService(int _serverPort) throws IllegalArgumentException, NumberFormatException, IOException {

		// Create a server socket to accept client connection requests
		System.out.println("Creating master service...");
		ServerSocket servSock = new ServerSocket(_serverPort);
		HistoricalDatabase hdb = new HistoricalDatabase("h.db");

		Logger logger = Logger.getLogger("pratical");
		int totalClientsConnected = 0;

		// Run forever, accepting and spawning a thread for each connection
		while (true) {
			System.out.println(String.format("Waiting for connections. Right now %d clients connected. ", totalClientsConnected));
			// Block waiting for connection
			Socket clntSock = servSock.accept();
			totalClientsConnected++;
			Thread thread = new Thread(new MonitoringMaster(clntSock,hdb));
			thread.start();
			logger.info("Created and started Thread " + thread.getName());
		}
		/* NOT REACHED */
	}
}