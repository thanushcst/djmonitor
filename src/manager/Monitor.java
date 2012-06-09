package manager;

import java.io.IOException;

/**
 * 
 * @author pmdusso
 */
public class Monitor {

	public static void main(String[] args) throws IOException {

		if (args.length < 1) {
			System.out.println("Utilizar:");
			System.out.println("monitor -m, para rodar MonitoringMaster");
			System.out.println("monitor -c, para rodar MonitoringClient");
		} else {
			for (String string : args) {
				if (string.replace("-", "").equals("m")) {
					MonitoringMaster mm = new MonitoringMaster();
				} else {
					MonitoringClient mc = new MonitoringClient(1000);
				}
			}

			System.out.println("Press Control-C to stop.");
		}
	}
}