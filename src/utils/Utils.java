package utils;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;
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
     * goes wrong.
     */
    public static int getPid() {
        try {
            byte[] bo = new byte[100];
            String[] cmd = {"bash", "-c", "echo $PPID"};
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
     * null.
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
     * wrong.
     */
    public static int getMacAddress() {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            Enumeration<NetworkInterface> nwInterface = NetworkInterface.getNetworkInterfaces();
            while (nwInterface.hasMoreElements()) {
                NetworkInterface nis = nwInterface.nextElement();
                if (nis != null) {
                    byte[] mac = nis.getHardwareAddress();
                    if (mac != null) {
                        /*
                         * Extract each array of mac address and generate a
                         * hashCode for it
                         */
                        return mac.hashCode();
                    } else {
                        Logger.getLogger(Utils.class.getName()).log(Level.WARNING, "Address doesn't exist or is not accessible");
                    }
                } else {
                    Logger.getLogger(Utils.class.getName()).log(Level.WARNING, "Network Interface for the specified address is not found.");
                }
                return -1;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
