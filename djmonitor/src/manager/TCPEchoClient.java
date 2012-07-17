package manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			System.out.println("Connected to server... sending echo string");
		} catch (UnknownHostException e) {
			Logger.getLogger(MonitoringClient.class.getName()).log(
					Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(MonitoringClient.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	public void TCPEchoClientSend(String _data) throws IOException {
		if (!Utils.stringNotEmpty(_data))
			throw new IllegalArgumentException("Parameter: <Data> empty.");

		this.data = _data.getBytes();
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		// Send the encoded string to the server
		out.write(this.data);

		System.out.println("Received: " + new String(data));

	}

	protected void finalize() throws Throwable {
		// do finalization here
		// Close the socket and its streams
		socket.close();

		super.finalize(); // not necessary if extending Object.
	}

}
