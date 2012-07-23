package utils;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			byte[] bo = new byte[100];
			String[] cmd = { "bash", "-c", "echo $PPID" };
			Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().read(bo);
			return Integer.parseInt(new String(bo).trim());
		} catch (IOException ex)
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
		} catch (NumberFormatException ex)
		{
			return false;
		}
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
		} catch (SocketException ex)
		{
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (ifs != null)
			while (ifs.hasMoreElements())
			{
				NetworkInterface iface = ifs.nextElement();
				System.out.println(iface.getName());
				Enumeration<InetAddress> en = iface.getInetAddresses();
				while (en.hasMoreElements())
				{
					InetAddress addr = en.nextElement();
					String s = addr.getHostAddress();
					int end = s.lastIndexOf("%");
					if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress())
						if (end > 0)
							System.out.println("\t" + s.substring(0, end));
						else
						{
							// System.out.println("\t" + s);
							return Math.abs(s.hashCode());
						}
				}
			}
		return -1;
	}
}
