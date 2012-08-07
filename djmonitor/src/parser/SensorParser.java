/**
 * 
 */
package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Symbols;

/**
 * @author pmdusso
 * 
 */
public class SensorParser
{
	/**
	 * Essa classe deve conter os atributos e métodos necessários para fazer a
	 * coleta de dados do sensor.
	 * 
	 * O único método publico, além do construtor, seria "gatherSensor", que
	 * seria chamado pela classe *NodeInfoGather*.
	 * 
	 */
	private Socket sensorSocket;

	/*
	 * Constructor of sensor parser.
	 */
	public SensorParser(String _stringAddress)
	{
		try
		{
			// Instantiate a socket with the sensor in the receive string
			// Address and the default port
			sensorSocket = new Socket(_stringAddress, 34318);
		} catch (final UnknownHostException e)
		{
			Logger.getLogger(SensorParser.class.getName()).log(Level.SEVERE,
					null, e);
		} catch (final IOException e)
		{
			Logger.getLogger(SensorParser.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	/**
	 * Return the sensored data.
	 * 
	 * @throws IOException
	 */
	public ArrayList<String> gatherSensor(int _numCanais) throws IOException
	{
		// Number of channels to be probed
		final int numCanais = _numCanais;

		sensorSocket.setKeepAlive(true);
		final BufferedReader r = new BufferedReader(new InputStreamReader(
				sensorSocket.getInputStream()));

		final PrintWriter w = new PrintWriter(sensorSocket.getOutputStream(),
				true);

		// Checking if it is connected
		System.out.println(sensorSocket.isBound());
		System.out.println(sensorSocket.isConnected());

		// Configuring the command with the number of channels
		final String channelNumber = "00" + String.valueOf(numCanais);
		w.println("FD0,001," + channelNumber);

		final ArrayList<String> sensoredData = new ArrayList<String>();
		String aux = Symbols.EMPTY;

		while ((aux = r.readLine()) != null)
		{
			if (!aux.equals("E0") && !aux.equals("EA") && !aux.equals("EN"))
			{
				sensoredData.add(aux);
				System.out.println(aux);
			}
			if (aux.equals("EN"))
				break;
		}

		return sensoredData;
	}

	@Override
	public void finalize() throws Throwable
	{
		sensorSocket.close();
	}
}
