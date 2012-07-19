package manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import parser.ProcParser;
import storage.HistoricalDatabase;
import usage.MonitoredData;

public class MonitoringMaster implements Runnable {

	// Socket connect to client
	private Socket clientSock;
	// Server logger
	private Logger logger;
	// Historical database to save the monitored data.
	HistoricalDatabase hdb;
	// Get the input and output I/O streams from socket
	ObjectInputStream ois;

	public MonitoringMaster(Socket clntSock, HistoricalDatabase _hdb) {
		this.clientSock = clntSock;
		this.hdb = _hdb;
		
		try {
			ois = new ObjectInputStream(this.clientSock.getInputStream());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception in echo protocol", e);
		}
	}

	public void handleEchoClient(Socket client, Logger logger) {
		try {
			MonitoredData mdata;
			// Receive until client closes connection, indicated by -1;
			while ((mdata = (MonitoredData) ois.readObject()) != null) {
				System.out.println("Got received data. Ready to save.");
				this.hdb.saveOrUpdate(mdata);
				System.out.println("Monitored Data arrived at home from node: " + String.valueOf(mdata.getNodeID()));
			}

		} catch (IOException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE, null, ex);
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
