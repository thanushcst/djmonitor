package manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoProtocol implements Runnable {

	// Tamanho (em bytes) do buffer de I/O
	private static final int BUFSIZE = 32;
	// Socket connect to client
	private Socket clientSock;
	// Server logger
	private Logger logger;

	public EchoProtocol(Socket clntSock, Logger logger) {
		this.clientSock = clntSock;
		this.logger = logger;
	}

	public static void handleEchoClient(Socket clntSock, Logger logger) {
		try {
			// Get the input and output I/O streams from socket
			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();

			// Size of received message
			int receiveMsgSize;
			// Bytes received from client
			int totalBytesEchoed = 0;
			// Receive Buffer
			byte[] echoBuffer = new byte[BUFSIZE];
			// Receive until client closes connection, indicated by -1;
			while ((receiveMsgSize = in.read(echoBuffer)) != -1) {
				out.write(echoBuffer, 0, receiveMsgSize);
				totalBytesEchoed += receiveMsgSize;
			}

			logger.info("Client " + clntSock.getRemoteSocketAddress()
					+ ", echoed " + totalBytesEchoed + " bytes.");

		} catch (IOException ex) {
			logger.log(Level.WARNING, "Exception in echo protocol", ex);
		} finally {
			try {
				clntSock.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void run() {
		handleEchoClient(clientSock, logger);
	}

}
