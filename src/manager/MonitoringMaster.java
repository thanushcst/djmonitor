package manager;

import storage.HistoricalDatabase;
import usage.MonitoredData;

public class MonitoringMaster {

    public MonitoringMaster() {
        new Receiver(this);
        new Saver(this);
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
        System.out.println("Got received data. Ready to save.");
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
        System.out.println("Received data. Ready to put it into DB.");
        notify();
    }
}

class Receiver implements Runnable {

    MonitoringMaster master;

    Receiver(MonitoringMaster m) {
        this.master = m;
        new Thread(this, "Receiver").start();
    }

    @Override
    public void run() {
        MonitoredData tempData;
        while (true) {
            System.out.println("Waiting for connection...");
            tempData = NodeInfoCommunicator.INSTANCE.Receive();
            master.put(tempData);
        }
    }
}

class Saver implements Runnable {

    MonitoringMaster master;
    HistoricalDatabase hdb =  new HistoricalDatabase("historical.db");
        
    Saver(MonitoringMaster m) {
        this.master = m;
        new Thread(this, "Saver").start();
    }

    @Override
    public void run() {
        MonitoredData tempData;
        while (true) {
            tempData = master.get();
            System.out.println("Monitored Data arrived at home...");
            hdb.saveOrUpdate(tempData);
        }
    }
}
