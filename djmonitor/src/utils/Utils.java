package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import usage.UnityType;

/**
 * 
 * @author pmdusso
 */
public enum Utils
{
	INSTANCE;
	/**
	 * Gets the PID of the current process.
	 * 
	 * @return The integer value of the current process PID; -1 if something
	 *         goes wrong.
	 */
	public static int getPid()
	{
		try
		{
			final byte[] bo = new byte[100];
			final String[] cmd = { "bash", "-c", "echo $PPID" };
			final Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().read(bo);
			return Integer.parseInt(new String(bo).trim());
		} catch (final IOException ex)
		{
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * Try to parse a string to a integer.
	 * 
	 * @return True if the string can be converted, false otherwise.
	 */
	public static boolean tryParseInt(String value)
	{
		try
		{
			Integer.parseInt(value);
			return true;
		} catch (final NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Remove Null Value from String array
	 */
	public static String[] removeEmptyStringsFromArray(String[] in)
	{
		final ArrayList<String> list = new ArrayList<String>();
		for (final String s : in)
			if (!s.equals(""))
				list.add(s);

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Test when a string is empty or null.
	 * 
	 * @return True if the string is not null nor empty; false if is empty or
	 *         null.
	 */
	public static boolean stringNotEmpty(String s)
	{
		return (s != null && s.length() > 0);
	}

	/**
	 * Generates a universally unique identifier and return it as a int value.
	 * The UUID is obtained from the node IP address.
	 * 
	 * @return A the absolutely value of the hash code of IP address.
	 */
	public static int getNodeUUID()
	{
		Enumeration<NetworkInterface> ifs = null;
		try
		{
			ifs = NetworkInterface.getNetworkInterfaces();
		} catch (final SocketException ex)
		{
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (ifs != null)
			while (ifs.hasMoreElements())
			{
				final NetworkInterface iface = ifs.nextElement();
				System.out.println(iface.getName());
				final Enumeration<InetAddress> en = iface.getInetAddresses();
				while (en.hasMoreElements())
				{
					final InetAddress addr = en.nextElement();
					final String s = addr.getHostAddress();
					final int end = s.lastIndexOf("%");
					if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress())
						if (end > 0)
							System.out.println("\t" + s.substring(0, end));
						else
							// System.out.println("\t" + s);
							return Math.abs(s.hashCode());
				}
			}
		return -1;
	}

	/**
	 * Get the maximum capacity of the network adapter.
	 */
	public static int getNetworkAdapterCapacity()
	{
		final List<String> command = new ArrayList<String>();
		command.add("lshw");
		command.add("-class");
		command.add("network");

		final ProcessBuilder builder = new ProcessBuilder(command);
		Process process;
		try
		{
			process = builder.start();

			final InputStream is = process.getInputStream();
			final InputStreamReader isr = new InputStreamReader(is);
			final BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null)
				if (line.contains("capacity"))
				{
					String[] splited = removeEmptyStringsFromArray(line
							.split(" "));
					if (splited[1].contains("M"))
						splited = splited[1].split("M");
					else if (splited[1].contains("G"))
						splited = splited[1].split("G");
					return Integer.valueOf(splited[0]);
				}
		} catch (final IOException e)
		{
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}
		return -1;
	}

	public static UnityType convertStringToUnity(String u)
	{
		if (u.equals(Symbols.CELCIUS))
			return UnityType.CELCIUS;
		else if (u.equals(Symbols.WATT))
			return UnityType.WATT;
		else if (u.equals(Symbols.PERCENT))
			return UnityType.PERCENT;
		else if (u.equals(Symbols.EMPTY))
			return UnityType.NULL;
		else if (u.equals(Symbols.MBITS))
			return UnityType.MBITS;
		else if (u.equals(Symbols.GBITS))
			return UnityType.GBITS;
		else
			return null;
	}
}
