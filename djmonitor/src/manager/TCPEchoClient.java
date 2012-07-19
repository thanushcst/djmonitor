package manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import usage.MonitoredData;
import utils.Utils;

public class TCPEchoClient {
	// Server name or IP address
	String server;
	// Convert argument String to bytes using the deafult character encoding
	byte[] data;
	// Port to connect to the server
	int servPort = 0;
	// Create socket that is connected to server on specified port
	Socket socket;
	
	ObjectOutputStream oos;

	public TCPEchoClient(String _server, int _servPort) {

		if (!Utils.stringNotEmpty(_server))
			throw new IllegalArgumentException("Parameter: <Server> empty.");

		if (_servPort <= 0)
			throw new IllegalArgumentException(
					"Parameter: <Server Port> empty.");

		this.server = _server;
		this.servPort = _servPort;

		try {
			socket = new Socket(server, servPort);
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Connected to server...");
		} catch (UnknownHostException e) {
			Logger.getLogger(MonitoringClient.class.getName()).log(
					Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(MonitoringClient.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	public void TCPEchoClientSend(MonitoredData _mData) throws IOException {
		if (_mData == null)
			throw new IllegalArgumentException("Parameter: <Monitored Data> empty.");

		// Cria um canal para enviar dados
		//ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		//ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	
		oos.reset();
		// Send the encoded object to the server
		//oos.writeObject(_mData);
		oos.writeUnshared(_mData);
		
		System.out.println("Client sent the monitored data package.");

	}

	protected void finalize() throws Throwable {
		// do finalization here
		// Close the socket and its streams
		socket.close();

		super.finalize(); // not necessary if extending Object.
	}

}
