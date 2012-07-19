package manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import storage.HistoricalDatabase;
import usage.MonitoredData;

public class EchoProtocol implements Runnable {

	// Tamanho (em bytes) do buffer de I/O
	private static final int BUFSIZE = 4046;
	// Socket connect to client
	private Socket clientSock;
	// Server logger
	private Logger logger;
	static// Historical database to sabe the monitored data.
	HistoricalDatabase hdb;

	public EchoProtocol(Socket clntSock, Logger logger) {
		this.clientSock = clntSock;
		this.logger = logger;
		this.hdb = new HistoricalDatabase("historical.db");
	}

	public static void handleEchoClient(Socket client, Logger logger) {
		try {
			MonitoredData mdata;
			// Get the input and output I/O streams from socket
			ObjectInputStream ois = new ObjectInputStream(client
					.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(client
					.getOutputStream());

			// Receive until client closes connection, indicated by -1;
			while ((mdata = (MonitoredData) ois.readObject()) != null) {

				System.out.println("Got received data. Ready to save.");

				hdb.saveOrUpdate(mdata);

				System.out.println("Monitored Data arrived at home number %d " + String.valueOf(mdata.hashCode()));

			}

			// logger.info("Client " + _clntSock.getRemoteSocketAddress()+
			// ", echoed " + totalBytesEchoed + " bytes.");

		} catch (IOException ex) {
			logger.log(Level.WARNING, "Exception in echo protocol", ex);
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "Exception in echo protocol", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void run() {
		handleEchoClient(clientSock, logger);
	}

}
