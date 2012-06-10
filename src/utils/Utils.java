package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author pmdusso
 */
public class Utils {

	/**
	 * Gets the PID of the current process.
	 * 
	 * @return The integer value of the current process PID; -1 if something
	 *         goes wrong.
	 */
	public static int getPid() {
		try {
			byte[] bo = new byte[100];
			String[] cmd = { "bash", "-c", "echo $PPID" };
			Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().read(bo);
			return Integer.parseInt(new String(bo).trim());
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * Try to parse a string to a integer.
	 * 
	 * @return True if the string can be converted, false otherwise.
	 */
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	/**
	 * Test when a string is empty or null.
	 * 
	 * @return True if the string is not null nor empty; false if is empty or
	 *         null.
	 * 
	 */
	public static boolean stringNotEmpty(String s) {
		return (s != null && s.length() > 0);
	}

	/**
	 * Get NetworkInterface for the current host and then read the hardware
	 * address.
	 * 
	 * @return The hashCode of the MAC address object; -1 if something goes
	 *         wrong.
	 */
	public static int getMacAddress(){
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e2);
		}
		ip.getHostAddress();

		NetworkInterface network = null;
		try {
			network = NetworkInterface.getByInetAddress(ip);
		} catch (SocketException e1) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e1);
		}

		byte[] mac = null;
		try {
			mac = network.getHardwareAddress();
		} catch (SocketException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i],
					(i < mac.length - 1) ? "-" : ""));
		}
		return sb.toString().hashCode();
	}
	public static int getMacAddress2(){
		Process p;
		try {
			p = Runtime.getRuntime().exec("getmac /fo csv /nh");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = in.readLine().split(",")[0].replace('"',' ');
			return line.hashCode();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
		
	}
}
