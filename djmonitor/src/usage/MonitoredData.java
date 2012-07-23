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
public class MonitoredData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7909922150038205539L;
    private int nodeID;
    private ArrayList<CpuData> cpu;
    private MemData mem;
    private ArrayList<DiskData> disk;
    private ArrayList<NetworkData> net;

    public MonitoredData(int iteracao, ArrayList<CpuData> _cpu, MemData _mem, ArrayList<DiskData> _disk, ArrayList<NetworkData> _net) {
        if (_cpu == null) {
            throw new ExceptionInInitializerError("CPU monitored data object is null");
        } else if (_mem == null) {
            throw new ExceptionInInitializerError("Memory monitored data object is null");
        } else if (_disk == null) {
            throw new ExceptionInInitializerError("Disk monitored data object is null");
        } else if (_net == null) {
            throw new ExceptionInInitializerError("Network monitored data object is null");
        }

        this.nodeID = iteracao; //Utils.getIPAddress();
        this.cpu = _cpu;
        this.mem = _mem;
        this.disk = _disk;
        this.net = _net;
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
    public List<DiskData> getDisk() {
        return this.disk;
    }

    /**
     * @return the net
     */
    public List<NetworkData> getNet() {
        return this.net;
    }
}
