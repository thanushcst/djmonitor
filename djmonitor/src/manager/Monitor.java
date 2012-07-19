package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import utils.Utils;

/**
 * 
 * @author pmdusso
 */
public class Monitor {

	public static void main(String[] args) throws IOException {
		String ipmask = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

		// Server address
		String serverAddress;
		// Server port. Use 4545 in case no port is passed.
		int echoServerPort = 4545;

		if (args.length < 1) {
			System.out.println("Utilizar:");
			System.out.println("monitor -m <PORT> para rodar MonitoringMaster");
			System.out
					.println("monitor -c <SERVER_ADDRESS> <PORT> para rodar MonitoringClient");
		} else {

			if (args[0].equals("-m")) {
				// * MASTER *//
				// MonitoringMaster mm = new MonitoringMaster();
				if (!Utils.stringNotEmpty(args[1]))
					throw new IllegalArgumentException(
							"Parameter: <Server Port> empty.");
				else
					echoServerPort = Integer.valueOf(args[1]);

				// Create a server socket to accept client connection requests
				System.out.println("Creating master service...");
				ServerSocket servSock = new ServerSocket(echoServerPort);

				Logger logger = Logger.getLogger("pratical");

				// Run forever, accepting and spawning a thread for each
				// connection
				while (true) {
					System.out.println("Waiting for connections...");
					// Block waiting for connection
					Socket clntSock = servSock.accept();
					Thread thread = new Thread(new EchoProtocol(clntSock, logger));
					thread.start();
					logger.info("Created and started Thread " + thread.getName());
				}
				/* NOT REACHED */

			} else {
				// * CLIENT *//
				if (!Utils.stringNotEmpty(args[1]))
					throw new IllegalArgumentException(
							"Parameter: <Server Address> empty.");
				else
					serverAddress = args[1];
				if (!Utils.stringNotEmpty(args[2]))
					throw new IllegalArgumentException(
							"Parameter: <Server Port> empty.");
				else
					echoServerPort = Integer.valueOf(args[2]);

				TCPEchoClient ec = new TCPEchoClient(serverAddress, echoServerPort);
				MonitoringClient mc = new MonitoringClient(1000, args[1], ec);
			}

			System.out.println("Press Control-C to stop.");
		}
	}
}