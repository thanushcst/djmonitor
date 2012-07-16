package manager;

import java.util.logging.Level;
import java.util.logging.Logger;
import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;
import usage.NetworkData;

public class MonitoringClient {

	public MonitoringClient(long _gatherInterval, String _masterIpAddr,
			int _iteracao) {
		new Gather(this, _gatherInterval, _iteracao);
		new Sender(this, _masterIpAddr);
	}

	MonitoredData mData;
	boolean valueSet = false;

	synchronized MonitoredData get() {
		if (!valueSet) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		System.out.println("Available data being used.");
		valueSet = false;
		notify();
		return this.mData;
	}

	synchronized void put(MonitoredData data) {
		if (valueSet) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		}
		this.mData = data;
		valueSet = true;
		System.out.println("Gathered data available.");
		notify();
	}
}

class Gather implements Runnable {

	MonitoringClient client;
	long gatherInterval = 0;
	int iteracao = 0;

	Gather(MonitoringClient client, long _gatherInterval, int _iteracao) {
		this.client = client;
		this.gatherInterval = _gatherInterval;
		this.iteracao = _iteracao;
		new Thread(this, "Gather").start();
	}

	@Override
	public void run() {
		MonitoredData tempData;
		while (true) {
			try {
				Thread.sleep(this.gatherInterval);
			} catch (InterruptedException ex) {
				Logger.getLogger(MonitoringClient.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			tempData = NodeInfoGather.INSTANCE.getSystemUsage(this.iteracao);
			System.out
					.println("Finished gathering data from procfs. Ready to send it.");
			client.put(tempData);
		}
	}
}

class Sender implements Runnable {

	String masterIpAddr;
	MonitoringClient client;

	Sender(MonitoringClient client, String _masterIpAddr) {
		this.client = client;
		this.masterIpAddr = _masterIpAddr;
		new Thread(this, "Sender").start();
	}

	@Override
	public void run() {
		MonitoredData tempData;

		while (true) {
			tempData = client.get();

			for (CpuData o : tempData.getCpu()) {
				System.out.print("CPU:");
				System.out.println(o.toString());
			}
			for (DiskData o : tempData.getDisk()) {
				System.out.print("DISK:");
				System.out.println(o.toString());
			}
			for (NetworkData o : tempData.getNet()) {
				System.out.print("NETWORK:");
				System.out.println(o.toString());
			}
			System.out.print("MEMORY:");
			System.out.println(tempData.getMem().toString());

			System.out.println("Got data already gathered. Sending...");
			// send to the master
			NodeInfoCommunicator.INSTANCE.SendTCP(tempData, this.masterIpAddr);
		}
	}
}