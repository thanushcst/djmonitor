package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import parser.ProcParser;
import usage.*;
import utils.Utils;

/**
 *
 * @author pmdusso
 */
public enum NodeInfoGather {

    INSTANCE;

    /**
     * Collect process system information from /proc file system.
     */
    public synchronized MonitoredData getSystemUsage() {
        MonitoredData mData = null;

        ProcParser pp = new ProcParser(Utils.getPid());

        mData = new MonitoredData(
                fillCpuData(pp.gatherUsage(UsageType.CPU)),
                fillMemData(pp.gatherUsage(UsageType.MEMORY)),
                fillDiskData(pp.gatherUsage(UsageType.DISK)),
                fillNetworkData(pp.gatherUsage(UsageType.NETWORK)));

        if (mData != null) {
            return mData;
        } else {
            Logger.getLogger(NodeInfoGather.class.getName()).log(Level.SEVERE, "Monitored Data object null");
            return null;
        }

    }

    /**
     * Creates a list of CPU objects; Each objects of this list correspond to a
     * CPU core. The first is the sum of all cores.
     */
    private ArrayList<CpuData> fillCpuData(ArrayList<String> gatheredData) {
        ArrayList<CpuData> c = new ArrayList<CpuData>();
        //c.add(new CpuData(coreId, user, nice, sysmode, idle, iowait, irq, softirq, steal, guest));
        int offset = 10;
        for (int base = 0; base < gatheredData.size(); base += offset) {
            c.add(new CpuData(Integer.parseInt(gatheredData.get(base)),
                    Integer.parseInt(gatheredData.get(base + 1)),
                    Long.parseLong(gatheredData.get(base + 2)),
                    Long.parseLong(gatheredData.get(base + 3)),
                    Long.parseLong(gatheredData.get(base + 4)),
                    Long.parseLong(gatheredData.get(base + 5)),
                    Long.parseLong(gatheredData.get(base + 6)),
                    Integer.parseInt(gatheredData.get(base + 7)),
                    Long.parseLong(gatheredData.get(base + 8)),
                    Long.parseLong(gatheredData.get(base + 9))));
        }
        return c;
    }

    /**
     * Creates a single Memory object corresponding to all values read from the
     * system.
     */
    private MemData fillMemData(List<String> gatheredData) {
        MemData m = new MemData(
                Integer.parseInt(gatheredData.get(0)),
                Integer.parseInt(gatheredData.get(1)),
                Integer.parseInt(gatheredData.get(2)),
                Integer.parseInt(gatheredData.get(3)),
                //Integer.parseInt(gatheredData.get(4)), not used
                Integer.parseInt(gatheredData.get(5)),
                //Integer.parseInt(gatheredData.get(6)), not used
                gatheredData.get(7),
                Integer.parseInt(gatheredData.get(8)),
                gatheredData.get(9),
                Integer.parseInt(gatheredData.get(10)),
                //used memory = total memory - free memory
                (Integer.parseInt(gatheredData.get(10)) - Integer.parseInt(gatheredData.get(11))),
                Integer.parseInt(gatheredData.get(11)),
                Integer.parseInt(gatheredData.get(12)),
                Integer.parseInt(gatheredData.get(13)));
        return m;
    }

    /**
     * Creates a list of Disk objects; Each object of this list correspond to a
     * disk physical partition.
     */
    private ArrayList<DiskData> fillDiskData(List<String> gatheredData) {
        ArrayList<DiskData> d = new ArrayList<DiskData>();
        int offset = 14;

        for (int base = 0; base < gatheredData.size(); base += offset) {
            d.add(new DiskData(gatheredData.get(base),
                    gatheredData.get(base + 1),
                    gatheredData.get(base + 2),
                    Long.parseLong(gatheredData.get(base + 3)),
                    Long.parseLong(gatheredData.get(base + 4)),
                    Long.parseLong(gatheredData.get(base + 5)),
                    Long.parseLong(gatheredData.get(base + 6)),
                    Long.parseLong(gatheredData.get(base + 7)),
                    Long.parseLong(gatheredData.get(base + 8)),
                    Long.parseLong(gatheredData.get(base + 9)),
                    Long.parseLong(gatheredData.get(base + 10)),
                    Long.parseLong(gatheredData.get(base + 11)),
                    Long.parseLong(gatheredData.get(base + 12)),
                    Long.parseLong(gatheredData.get(base + 13))));
        }

        return d;
    }

    /**
     * Creates a list of Network objects; Each object of this list correspond to
     * a network interface.
     */
    private ArrayList<NetworkData> fillNetworkData(List<String> gatheredData) {
        ArrayList<NetworkData> n = new ArrayList<NetworkData>();
        int offset = 17;
        for (int base = 0; base < gatheredData.size(); base += offset) {
            // std::string, long int, int, int, int, int, int, int, int, long int, int, int, int, int, int, int, int)
            n.add(new NetworkData(gatheredData.get(base),
                    Long.parseLong(gatheredData.get(base + 1)),
                    Integer.parseInt(gatheredData.get(base + 2)),
                    Integer.parseInt(gatheredData.get(base + 3)),
                    Integer.parseInt(gatheredData.get(base + 4)),
                    Integer.parseInt(gatheredData.get(base + 5)),
                    Integer.parseInt(gatheredData.get(base + 6)),
                    Integer.parseInt(gatheredData.get(base + 7)),
                    Integer.parseInt(gatheredData.get(base + 8)),
                    Long.parseLong(gatheredData.get(base + 9)),
                    Integer.parseInt(gatheredData.get(base + 10)),
                    Integer.parseInt(gatheredData.get(base + 11)),
                    Integer.parseInt(gatheredData.get(base + 12)),
                    Integer.parseInt(gatheredData.get(base + 13)),
                    Integer.parseInt(gatheredData.get(base + 14)),
                    Integer.parseInt(gatheredData.get(base + 15)),
                    Integer.parseInt(gatheredData.get(base + 16))));
        }
        return n;
    }
}
