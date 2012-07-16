package manager;

import java.io.IOException;

/**
 * 
 * @author pmdusso
 */
public class Monitor {

	public static void main(String[] args) throws IOException {
		String ipmask = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

		if (args.length < 1) {
			System.out.println("Utilizar:");
			System.out.println("monitor -m, para rodar MonitoringMaster");
			System.out
					.println("monitor -c -a IP_ADDRESS -i ITERACAO , para rodar MonitoringClient");
		} else {

			if (args[0].equals("-m")) {
				MonitoringMaster mm = new MonitoringMaster();
			} else {
				MonitoringClient mc = new MonitoringClient(1000, args[2], Integer.valueOf(args[4]));
			}
			System.out.println("Press Control-C to stop.");
		}
	}
}