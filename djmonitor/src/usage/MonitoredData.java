/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pmdusso
 */
public class MonitoredData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7909922150038205539L;
    private int nodeID;
    private ArrayList<CpuData> cpu;
    private MemData mem;
    private Map<String, DiskData> disk;
    private Map<String, NetworkData> net;

    public MonitoredData(int iteracao, ArrayList<CpuData> _cpu, MemData _mem, Map<String, DiskData> _disk, Map<String, NetworkData> map) {
        if (_cpu == null) {
            throw new ExceptionInInitializerError("CPU monitored data object is null");
        } else if (_mem == null) {
            throw new ExceptionInInitializerError("Memory monitored data object is null");
        } else if (_disk == null) {
            throw new ExceptionInInitializerError("Disk monitored data object is null");
        } else if (map == null) {
            throw new ExceptionInInitializerError("Network monitored data object is null");
        }

        this.nodeID = iteracao; //Utils.getIPAddress();
        this.cpu = _cpu;
        this.mem = _mem;
        this.disk = _disk;
        this.net = map;
    }

    public int getNodeID() {
        return this.nodeID;
    }

    /**
     * @return the cpu
     */
    public List<CpuData> getCpu() {
        return this.cpu;
    }

    /**
     * @return the mem
     */
    public MemData getMem() {
        return this.mem;
    }

    /**
     * @return the disk
     */
    public Map<String, DiskData> getDisk() {
        return this.disk;
    }

    /**
     * @return the net
     */
    public Map<String, NetworkData> getNet() {
        return this.net;
    }
}
