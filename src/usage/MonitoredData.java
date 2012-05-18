/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmdusso
 */
public class MonitoredData implements Serializable{

    private ArrayList<CpuData> cpu;
    private MemData mem;
    private ArrayList<DiskData> disk;
    private ArrayList<NetworkData> net;

    public MonitoredData(ArrayList<CpuData> _cpu, MemData _mem, ArrayList<DiskData> _disk, ArrayList<NetworkData> _net) {
        this.cpu = _cpu;
        this.mem = _mem;
        this.disk = _disk;
        this.net = _net;
    }

    /**
     * @return the cpu
     */
    public List<CpuData> getCpu() {
        return cpu;
    }

    /**
     * @return the mem
     */
    public MemData getMem() {
        return mem;
    }

    /**
     * @return the disk
     */
    public List<DiskData> getDisk() {
        return disk;
    }

    /**
     * @return the net
     */
    public List<NetworkData> getNet() {
        return net;
    }
}
